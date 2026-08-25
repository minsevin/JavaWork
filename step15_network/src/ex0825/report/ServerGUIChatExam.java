package ex0825.report;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerGUIChatExam {

    List<ClientSkThread> list = new ArrayList<>();
    Socket sk;
    public ServerGUIChatExam() {
        try (ServerSocket server = new ServerSocket(8002)) {
            while (true) {
                System.out.println("Client접속 대기중입니다.^^");
                sk = server.accept();

                ClientSkThread th = new ClientSkThread();
                th.start();
                list.add(th);

                System.out.println(sk.getInetAddress() + "님 접속하셨습니다.^^");
                System.out.println("현재 접속 인원 : " + list.size() + "명\n");
 
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 모든 client에게 데이터 전송
     */
    public void sendMessage(String message) {
        for (ClientSkThread th : list) {
            th.pw.println(message);
        }
    }

    /**
     * 닉네임 중복 확인
     */
    public boolean isDuplicateNickname(String name, ClientSkThread currentThread) {
        for (ClientSkThread th : list) {
            // 자기 자신은 제외
            if (th != currentThread && name.equals(th.nickName)) {
                return true;
            }
        }
        return false;
    }

    /////////////////////////////////////////////

    class ClientSkThread extends Thread {

        PrintWriter pw;
        BufferedReader br;
        String nickName;

        ClientSkThread() {

            try {
                pw = new PrintWriter(sk.getOutputStream(), true);
                br = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        @Override
        public void run() {
            try {

                /*
                 * 닉네임 중복이 아닐 때까지 반복
                 */
                while (true) {

                    nickName = br.readLine();

                    // 닉네임 중복 확인
                    if (isDuplicateNickname(nickName, this)) {

                        pw.println("DUPLICATE");

                    }else{

                        pw.println("OK");

                        break;
                    }
                }

                // 입장 메시지
                sendMessage("[" + nickName + "]님 입장하셨습니다.");

                while (true) {
                    String inputData = br.readLine();
                    if (inputData == null) break;
                    sendMessage("[" + nickName + "] " + inputData);
                }

            } catch (Exception e) {



            } finally {

                list.remove(this);
                sendMessage("[" + nickName + "]님 퇴장 하셨습니다.");
                System.out.println("[" + nickName + "]님 퇴장 : 현재인원 = "+ list.size() + "명");
            }
        }
    }

    public static void main(String[] args) {
        new ServerGUIChatExam();

    }
}
