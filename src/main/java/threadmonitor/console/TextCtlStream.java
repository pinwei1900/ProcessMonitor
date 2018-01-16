package threadmonitor.console; /**
 * Copyright (C) 2015 uphy.jp Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import javafx.application.Platform;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


/**
 * @author Yuhi Ishikura
 */
class TextCtlStream {

    private final TextCtlInputStream in;
    private final TextCtlOutputStream out;
    private final Charset charset;

    TextCtlStream(final TextInputControl textInputControl, Charset charset) {
        this.charset = charset;
        this.in = new TextCtlInputStream(textInputControl);
        this.out = new TextCtlOutputStream(textInputControl);

        textInputControl.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                getIn().enterKeyPressed();
                return;
            }
            if (textInputControl.getCaretPosition() <= getIn().getLastLineBreakIndex()) {
                e.consume();
            }
        });
        textInputControl.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            if (textInputControl.getCaretPosition() < getIn().getLastLineBreakIndex()) {
                e.consume();
            }
        });
    }

    void clear() throws IOException {
        this.in.clear();
        this.out.clear();
    }
    void startProgramInput() {
        // do nothing
    }
    void endProgramInput() {
        getIn().moveLineStartToEnd();
    }
    Charset getCharset() {
        return this.charset;
    }

    class TextCtlInputStream extends InputStream {

        private final TextInputControl textInputControl;
        private final PipedInputStream outputTextSource;
        private final PipedOutputStream inputTextTarget;
        private int lastLineBreakIndex = 0;

        TextCtlInputStream(TextInputControl textInputControl) {
            this.textInputControl = textInputControl;
            this.inputTextTarget = new PipedOutputStream();
            try {
                this.outputTextSource = new PipedInputStream(this.inputTextTarget);
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }
        int getLastLineBreakIndex() {
            return this.lastLineBreakIndex;
        }
        void moveLineStartToEnd() {
            this.lastLineBreakIndex = this.textInputControl.getLength();
        }
        void enterKeyPressed() {
            synchronized (this) {
                try {
                    this.textInputControl.positionCaret(this.textInputControl.getLength());
                    String lastLine = getLastLine() + " | sed -r \"s:\\x1B\\[[0-9;]*[mK]::g\"";
                    final ByteBuffer buf = getCharset().encode(lastLine + "\r\n "); //$NON-NLS-1$
                    this.inputTextTarget.write(buf.array(), 0, buf.remaining());
                    this.inputTextTarget.flush();
                    this.lastLineBreakIndex = this.textInputControl.getLength() + 1;
                } catch (IOException e) {
                    if ("Read end dead".equals(e.getMessage())) {
                        return;
                    }
                    throw new RuntimeException(e);
                }
            }
        }
        private String getLastLine() {
            synchronized (this) {
                return this.textInputControl.getText(this.lastLineBreakIndex, this.textInputControl.getLength());
            }
        }
        @Override
        public int available() throws IOException {
            return this.outputTextSource.available();
        }
        @Override
        public int read() throws IOException {
            try {
                return this.outputTextSource.read();
            } catch (IOException ex) {
                return -1;
            }
        }
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            try {
                return this.outputTextSource.read(b, off, len);
            } catch (IOException ex) {
                return -1;
            }
        }
        @Override
        public int read(final byte[] b) throws IOException {
            try {
                return this.outputTextSource.read(b);
            } catch (IOException ex) {
                return -1;
            }
        }
        @Override
        public void close() throws IOException {
            super.close();
        }
        void clear() throws IOException {
            this.inputTextTarget.flush();
            this.lastLineBreakIndex = 0;
        }
    }


    class TextCtlOutputStream extends OutputStream {

        private final TextInputControl textInputControl;
        private final CharsetDecoder decoder;
        private ByteArrayOutputStream buf;

        TextCtlOutputStream(TextInputControl textInputControl) {
            this.textInputControl = textInputControl;
            this.decoder = getCharset().newDecoder();
        }
        @Override
        public synchronized void write(int b) throws IOException {
            synchronized (this) {
                if (this.buf == null) {
                    this.buf = new ByteArrayOutputStream();
                }
                this.buf.write(b);
            }
        }
        @Override
        public void flush() throws IOException {
            Platform.runLater(() -> {
                try {
                    flushImpl();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        private void flushImpl() throws IOException {
            synchronized (this) {
                if (this.buf == null) {
                    return;
                }
                startProgramInput();
                final ByteBuffer byteBuf = ByteBuffer.wrap(this.buf.toByteArray());
                final CharBuffer charBuf = this.decoder.decode(byteBuf);
                try {
                    this.textInputControl.appendText(charBuf.toString());
                    this.textInputControl.positionCaret(this.textInputControl.getLength());
                } finally {
                    this.buf = null;
                    endProgramInput();
                }
            }
        }
        @Override
        public void close() throws IOException {
            flush();
        }
        void clear() throws IOException {
            this.buf = null;
        }
    }
    TextCtlInputStream getIn() {
        return this.in;
    }
    TextCtlOutputStream getOut() {
        return this.out;
    }
}
