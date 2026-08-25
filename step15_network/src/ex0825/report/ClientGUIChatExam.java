package ex0825.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUIChatExam extends JFrame {

    JTextArea textArea = new JTextArea();
    JTextField textField = new JTextField();
    JScrollPane scrollPane = new JScrollPane(textArea);

    Socket sk;
    PrintWriter pw;
    BufferedReader br;

    public ClientGUIChatExam() {

        super("채팅프로그램");

        Container con = getContentPane();

        // 컴포넌트 추가
        con.add(textField, BorderLayout.SOUTH);
        con.add(scrollPane, BorderLayout.CENTER);

        // 옵션 설정
        textArea.setFocusable(false);//커서 놓기 안됨
        textArea.setBackground(Color.CYAN);

        // 창크기
        setSize(500, 400);

        // 정중앙
        setLocationRelativeTo(null);

        // 보여주기
        setVisible(true);

        // X 클릭했을 때 종료
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 서버 연결
        connection();

        // JTextField에 입력 후 Enter
        textField.addActionListener((e) -> {

            String input = textField.getText();

            pw.println(input);

            textField.setText("");

        });

        // 서버 메시지 받기
        new Thread(() -> {
            try {
                while (true) {
                    String data = br.readLine();

                    if (data == null)
                        break;

                    textArea.append(data + "\n");

                    textArea.setCaretPosition(textArea.getText().length());
                }

            } catch (Exception e) {
            	e.printStackTrace();
            }

        }).start();

    }

    /**
     * 서버 접속 요청
     */
    public void connection() {
        try {

            sk = new Socket("121.137.21.66", 8002);
            br = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            pw = new PrintWriter(sk.getOutputStream(), true);

            String name;

            /*
             * 닉네임이 중복되지 않을 때까지 반복
             */
            while(true){

                name = JOptionPane.showInputDialog(this,"대화명을 입력하세요");

                // 서버에 닉네임 전송
                pw.println(name);

                // 서버의 응답 받기
                String result = br.readLine();

                if (result.equals("duplication")) {
                	JOptionPane.showMessageDialog(this,"닉네임이 중복되었습니다.");

                }else if (result.equals("OK")) {

                    break;
                }
            }

            // 닉네임 설정
            setTitle("[" + name + "]");

        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientGUIChatExam();

    }
}
