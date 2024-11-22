package cliente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TODO: Complementa esta clase para que genere la conexi�n TCP con el servidor
 * para enviar un boleto, recibir la respuesta y finalizar la sesion
 */
public class ClienteTCP {
	private Socket socketCliente = null;
	private BufferedReader entrada = null;
	private PrintWriter salida = null;
	
	public ClienteTCP(String ip, int puerto) {
		try {
			// Creo el socket para que el cliente se comunique con el servidor, indicandole la ip y el puerto del mismo
			socketCliente = new Socket(ip, puerto);
			System.out.println("Conexi�n establecida: " + socketCliente);
			// Creo una entrada y una salida de datos para que el cliente se pueda comunicar con el servidor
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.err.printf("Imposible conectar con ip:%s / puerto:%d",ip,puerto);
			System.exit(-1);
		}
	}

	public String comprobarBoleto(int[] combinacion) {
		String respuesta = "";
		String boletoString = "";
		// Transformo el array combinacion en una cadena de texto para pasársela al servidor
		for(int i = 0; i < combinacion.length; i++) {
			boletoString = boletoString + " " + combinacion[i];
		}
		// Me aseguro de que el formato sea correcto con un trim
		boletoString = boletoString.trim();
		// Le envío el boleto al servidor
		salida.println(boletoString);
		try {
			// Tomo la respuesta del servidor tras el análisis del boleto
			respuesta = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Devuelvo la respuesta del servidor
		return respuesta;
	}

	public void finSesion () {
		try {
			// Le envío al servidor la palabra fin para que termine su ejecución
			salida.println("fin");
			// Cierro la entrada y la salida de datos del cliente con el servidor
			salida.close();
			entrada.close();
			// Cierro el socket del cliente, finalizando su comunicación con el servidor
			socketCliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Cliente Terminado");
	}
	
}
