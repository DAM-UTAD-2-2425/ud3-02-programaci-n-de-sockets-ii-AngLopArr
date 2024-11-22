package servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Complementa esta clase para que acepte conexiones TCP con clientes
 * para recibir un boleto, generar la respuesta y finalizar la sesion
 */
public class ServidorTCP {
	private Socket socketCliente;
	private ServerSocket socketServidor;
	private BufferedReader entrada;
	private PrintWriter salida;
	private String [] respuesta;
	private int [] combinacion;
	private int reintegro;
	private int complementario;

	public ServidorTCP (int puerto) {
		this.respuesta = new String[9];
		this.respuesta[0] = "Boleto invalido - Numeros repetidos";
		this.respuesta[1] = "Boleto invalido - Numeros incorretos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";
		generarCombinacion();
		imprimirCombinacion();
		try {
			// Creo el socket del servidor y le asigno un puerto
			socketServidor = new ServerSocket(puerto);
			System.out.println("Esperando conexion...");
			// Espero a la conexión del cliente y cuando llega, creo un nuevo socket para que el servidor se comunique con él
			socketCliente = socketServidor.accept();
			System.out.println("Conexion acceptada: " + socketCliente);
			// Creo una entrada de datos y una salida de datos para que el servidor pueda comunicarse con el cliente
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("No puede escuchar en el puerto: " + puerto);
			System.exit(-1);
		}
	}
	
	public String leerCombinacion () {
		// Creo un string que almacene la cadena que mande el cliente
		String boleto = "";
		try {
			// Tomo a través de la entrada la cadena que mande el cliente
			boleto = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Retorno la cadena del cliente que contiene el bolete que este ha decidido jugar
		return boleto;
	}
	
	public String comprobarBoleto(String boleto) {
		// Creo una serie de variables que me ayudarán a determinar la validez del boleto y los aciertos del cliente
		int contadorAciertos = 0;
		boolean numeroRepetido = false;
		boolean numeroIncorrecto = false;
		boolean aciertoComplementario = false;
		boolean aciertoReintegro = false;
		// Transformo el boleto que ha pasado el cliente en un array
		String[] boletoArray = boleto.split(" ");
		// Recorro dicho array
		for(int i = 0; i < boletoArray.length; i++) {
			for(int j = 0; j < boletoArray.length; j++) {
				// Sin tener en cuenta aquellos número que son iguales por estar en la misma posición, es decir, ser el mismo
				if(i != j) {
					// Compruebo si alguno de los número está repetido
					if(Integer.parseInt(boletoArray[i]) == Integer.parseInt(boletoArray[j])) {
						numeroRepetido = true;
					}
				}
			}
			// A su vez compruebo que todos los números se encuentren en el rango correcto
			if(Integer.parseInt(boletoArray[i]) < 1 || Integer.parseInt(boletoArray[i]) > 49) {
				numeroIncorrecto = true;
			}
		}
		// Si no hay ningún número repetido
		if(!numeroRepetido) {
			// Y todos los números están dentro del rango válido
			if(!numeroIncorrecto) {
				// Compruebo los aciertos del cliente
				for(int i = 0; i < boletoArray.length; i++) {
					for(int j = 0; j < this.combinacion.length; j++) {
						// Recorro tanto el array con la jugada del cliente como aquel que corresponde a la combinación ganadora
						if(Integer.parseInt(boletoArray[i]) == this.combinacion[j]) {
							// Anoto cada coincidencia, es decir, cada acierto
							contadorAciertos++;
						}
					}
					// Compruebo, a su vez, si alguno de los números coincide con el completementario
					if(Integer.parseInt(boletoArray[i]) == this.complementario) {
						aciertoComplementario = true;
					}
					// O con el reintegro
					if(Integer.parseInt(boletoArray[i]) == this.reintegro) {
						aciertoReintegro = true;
					}
				}
				// Hago un switch para comprobar los aciertos del jugador y devuelvo la respuesta  que corresponda según los aciertos
				switch(contadorAciertos) {
				case 6:
						return this.respuesta[2];
				case 5:
					if(aciertoComplementario) {
						return this.respuesta[3];
					}
					else {
						return this.respuesta[4];
					}
				case 4:
					return this.respuesta[5];
				case 3:
					return this.respuesta[6];
				default:
					if(aciertoReintegro) {
						return this.respuesta[7];
					}
					else {
						return this.respuesta[8];
					}
				}
			}
			else {
				// Si hay número fuera de rango, envío una respuesta adecuada
				return this.respuesta[1];
			}
		}
		else {
			// Si hay números repetidos, envío una respuesta adecuada
			return this.respuesta[0];
		}
	}

	public void enviarRespuesta (String respuesta) {
		// Le mando la respuesta a su jugada al cliente
		salida.println(respuesta);
	}
	
	public void finSesion () {
		try {
			// Cierro la entrada y salida de datos, así como el socket con el cliente y el socket de escucha del servidor
			salida.close();
			entrada.close();
			socketCliente.close();
			socketServidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Servidor Terminado");
	}
	
	private void generarCombinacion () {
		Set <Integer> numeros = new TreeSet <Integer>();
		Random aleatorio = new Random ();
		while (numeros.size()<6) {
			numeros.add(aleatorio.nextInt(49) + 1);
		}
		int i = 0;
		this.combinacion = new int [6];
		for (Integer elto : numeros) {
			this.combinacion[i++] = elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}
	
	private void imprimirCombinacion () {
		System.out.print("Combinacion ganadora: ");
		for (Integer elto : this.combinacion) 
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}
}

