package cliente;

import java.util.InputMismatchException;
import java.util.Scanner;


/**
 * No debes tocar este c�digo
 */
public class PrimitivaCliente {
	public static void main(String[] args) {
		Scanner sc = new Scanner (System.in);
		ClienteTCP canal = new ClienteTCP("localhost",5555);
		int [] combi = new int [6];
		System.out.println("Bienvenido a la Loteria Primitiva");
		do {
			System.out.println("Escriba su combinacion de 6 numeros. Uno por linea.");
			try {
				for (int i = 0; i < combi.length; i++) {
					combi[i] = sc.nextInt();
				}
				// Imprimo el resultado del boleto enviado
				System.out.println(canal.comprobarBoleto(combi));
				System.out.println("¿Desea volver a jugar? (s/n)");
			}
			catch(InputMismatchException e) {
				// Añado una excepción en caso de que el cliente haya introducido un dato diferente a un número entero
				System.out.println("Has introducido un dato no válido.");
				break;
			}
		} while (sc.next().toLowerCase().equals("s"));
		canal.finSesion();
	}
}
