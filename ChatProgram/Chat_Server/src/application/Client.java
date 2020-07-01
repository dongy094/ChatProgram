package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

public class Client {

	Socket socket; // socket이 있어야 네트워크 환경에서 다른 컴퓨터와 통신 가능
	
	public Client(Socket socket) {
		this.socket = socket;
		receive(); //receive를 통해서 client로 부터 반복적으로 메세지를 받을 수 있게
	}
	
	// 클라이언트로부터 메세지를 전달 받는 메소드
	public void receive() {
		Runnable thread = new Runnable() { //하나의 스레드 만들때 일반적으로 Runnable 객체를 사용한다.
			
			@Override
			public void run() {
				try {
					while(true) { // 반복적으로 클라이언트로 부터 내용을 받을 수 있게
						InputStream in = socket.getInputStream(); //내용을 전달받을 수 있게
						byte[] buffer = new byte[512]; //버퍼 설정해서 한번에 512바이트 만큼 전달 받을 수 있게 한다.
						int length = in.read(buffer); //읽은 데이터를 버퍼에 담아준다????
						while(length == -1 ) throw new IOException();
						System.out.println("[메세지 수신 성공]" 
								+ socket.getRemoteSocketAddress() //접속한 클라이언트의 주소정보
								+ " : "+Thread.currentThread().getName()); // 스레드 고유정보
						String message = new String(buffer,0,length,"UTF-8");
						System.out.println("수신함ok1?");
						for(Client client : Main.clients) { //클라이언트 여러명?에게 메세지를 보내는 것
										   //Vector<Client> clients = new Vector<Client>();
							System.out.println("수신함ok2?");
							client.send(message);
						}
					}
				} catch (Exception e) {
					try {
						System.out.println("[메세지 수신 오류]" 
								+ socket.getRemoteSocketAddress() 
								+ " : "+Thread.currentThread().getName());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		// 안정적으로 운영할 수 있게 하나의 생성되 스레드를 스레드풀에 등록한다.
		Main.threadPool.submit(thread); 
		
	}
	
	// 클라이언트에게 메세지를 전송 하는 메소드
	public void send(String message) {
		System.out.println("수신함ok?3");
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				System.out.println("수신함ok5?");
				try {
					System.out.println("수신함ok6?");
					OutputStream out = socket.getOutputStream(); //  입력 받을 떄는 인풋스트림,  출력할떄는 아웃풋스트림
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer); // 버퍼에 담긴내용을 서버에서 클라이언트로 전송
					out.flush(); // 성공적으로 여기까지 했다는 것을 알려준다.
					System.out.println("수신함ok7?");
				}catch (Exception e) {
					try {
						System.out.println("[메세지 송신 오류]"
								+ socket.getRemoteSocketAddress()
								+" : " + Thread.currentThread().getName());
						Main.clients.remove(Client.this); // 클라이언트에서 오류가 발생하면 접속된 해당 클라이언트를 서버에서 지워준다. 
						socket.close(); // 오류생긴 클라이언트 소켓 종료
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	}
}