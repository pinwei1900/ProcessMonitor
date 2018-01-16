package threadmonitor.console; /**
 * Copyright (C) 2015 uphy.jp Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License
 * at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */


import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;


/**
 * @author Yuhi Ishikura
 */
public class ConsoleView extends BorderPane {
    private final TextArea textArea;

    public ConsoleView() {
        /* 设置中心地带*/
        textArea = new TextArea();
        textArea.setWrapText(true);

        /* 获取文本控制流*/
        final TextCtlStream stream = new TextCtlStream(textArea, Charset.defaultCharset());

        /* 获取菜单目录*/
        final ContextMenu menu = getContextMenu(stream);

        /* 设置大小*/
        setCenter(textArea);
        textArea.setContextMenu(menu);

        /* 设置快捷键*/
        KeyBindingUtils.installEmacsKeyBinding(textArea);

        /* 设置系统输入输出错误流*/
        try {
            PrintStream out = new PrintStream(stream.getOut(), true, Charset.defaultCharset().name());


//            System.setIn(stream.getIn());
//            System.setOut(out);
//            System.setErr(out);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private ContextMenu getContextMenu(TextCtlStream stream) {
        final ContextMenu menu = new ContextMenu();
        menu.getItems().add(createItem(e -> {
            try {
                stream.clear();
                this.textArea.clear();
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        }));
        return menu;
    }
    private MenuItem createItem(EventHandler<ActionEvent> a) {
        final MenuItem menuItem = new MenuItem("");
        menuItem.setOnAction(a);
        return menuItem;
    }
}
