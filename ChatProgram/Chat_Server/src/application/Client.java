package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

public class Client {

	Socket socket; // socket�� �־�� ��Ʈ��ũ ȯ�濡�� �ٸ� ��ǻ�Ϳ� ��� ����
	
	public Client(Socket socket) {
		this.socket = socket;
		receive(); //receive�� ���ؼ� client�� ���� �ݺ������� �޼����� ���� �� �ְ�
	}
	
	// Ŭ���̾�Ʈ�κ��� �޼����� ���� �޴� �޼ҵ�
	public void receive() {
		Runnable thread = new Runnable() { //�ϳ��� ������ ���鶧 �Ϲ������� Runnable ��ü�� ����Ѵ�.
			
			@Override
			public void run() {
				try {
					while(true) { // �ݺ������� Ŭ���̾�Ʈ�� ���� ������ ���� �� �ְ�
						InputStream in = socket.getInputStream(); //������ ���޹��� �� �ְ�
						byte[] buffer = new byte[512]; //���� �����ؼ� �ѹ��� 512����Ʈ ��ŭ ���� ���� �� �ְ� �Ѵ�.
						int length = in.read(buffer); //���� �����͸� ���ۿ� ����ش�????
						while(length == -1 ) throw new IOException();
						System.out.println("[�޼��� ���� ����]" 
								+ socket.getRemoteSocketAddress() //������ Ŭ���̾�Ʈ�� �ּ�����
								+ " : "+Thread.currentThread().getName()); // ������ ��������
						String message = new String(buffer,0,length,"UTF-8");
						System.out.println("������ok1?");
						for(Client client : Main.clients) { //Ŭ���̾�Ʈ ������?���� �޼����� ������ ��
										   //Vector<Client> clients = new Vector<Client>();
							System.out.println("������ok2?");
							client.send(message);
						}
					}
				} catch (Exception e) {
					try {
						System.out.println("[�޼��� ���� ����]" 
								+ socket.getRemoteSocketAddress() 
								+ " : "+Thread.currentThread().getName());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		// ���������� ��� �� �ְ� �ϳ��� ������ �����带 ������Ǯ�� ����Ѵ�.
		Main.threadPool.submit(thread); 
		
	}
	
	// Ŭ���̾�Ʈ���� �޼����� ���� �ϴ� �޼ҵ�
	public void send(String message) {
		System.out.println("������ok?3");
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				System.out.println("������ok5?");
				try {
					System.out.println("������ok6?");
					OutputStream out = socket.getOutputStream(); //  �Է� ���� ���� ��ǲ��Ʈ��,  ����ҋ��� �ƿ�ǲ��Ʈ��
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer); // ���ۿ� ��䳻���� �������� Ŭ���̾�Ʈ�� ����
					out.flush(); // ���������� ������� �ߴٴ� ���� �˷��ش�.
					System.out.println("������ok7?");
				}catch (Exception e) {
					try {
						System.out.println("[�޼��� �۽� ����]"
								+ socket.getRemoteSocketAddress()
								+" : " + Thread.currentThread().getName());
						Main.clients.remove(Client.this); // Ŭ���̾�Ʈ���� ������ �߻��ϸ� ���ӵ� �ش� Ŭ���̾�Ʈ�� �������� �����ش�. 
						socket.close(); // �������� Ŭ���̾�Ʈ ���� ����
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadPool.submit(thread);
	}
}