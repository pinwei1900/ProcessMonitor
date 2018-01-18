package threadmonitor.services;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import net.schmizz.keepalive.KeepAliveProvider;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.common.LoggerFactory;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class SshService {

    private static SSHClient ssh;
    private String host = "119.23.200.215";
    private String username = "root";
    private String password = "Hsl13571676728";

    public SshService() {
        initSsh();
    }

    public SshService(String host,String username,String password){
        this.host = host;
        this.username = username;
        this.password = password;
        initSsh();
    }

    private void initSsh(){
        DefaultConfig defaultConfig = new DefaultConfig();
        defaultConfig.setKeepAliveProvider(KeepAliveProvider.KEEP_ALIVE);
        ssh = new SSHClient(defaultConfig);
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        try {
            ssh.connect(host);
            ssh.getConnection().getKeepAlive().setKeepAliveInterval(5);
            ssh.authPassword(username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            ssh.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String excute(String command) {
        try {
            try (Session session = ssh.startSession()) {
                final Command cmd = session.exec(command);
                String result = IOUtils.readFully(cmd.getInputStream()).toString();
                cmd.join(5, TimeUnit.SECONDS);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static void main(String[] args) {
        SshService sshService = new SshService();
        String result = sshService.querySysInfo();
        System.out.println(result);
    }

    public String querySysInfo() {
        StringBuilder sysInfo = new StringBuilder();
        String version = excute("head -n 1 /etc/issue");
        String hostname = excute("hostname");
        String mem = excute("free -m");
        String df = excute("df -h");
        sysInfo.append(version).append(hostname).append(mem).append(df);
        return sysInfo.toString();
    }

    public String queryProcessInfo(String conmand, String pid) {
        StringBuilder procInfo = new StringBuilder();
        String exe = excute("ls -l /proc/" + pid + "/exe|awk '{print $NF}'");
        String cwd = excute("ls -l /proc/" + pid + "/cwd|awk '{print $NF}'");
        procInfo.append("命令").append(conmand + "\n").append("执行：").append(exe).append("路径").append(cwd);
        return procInfo.toString();
    }
}
