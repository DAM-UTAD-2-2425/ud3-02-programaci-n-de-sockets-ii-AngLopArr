package servidor;

public class PrimitivaServidor {

	public static void main(String[] args) {
		ServidorTCP canal = new ServidorTCP(5555);
		String linea;
		String respuesta;
		do {
			linea = canal.leerCombinacion();
			// Si el cliente nos pasa una linea indicando el final, no se ejecutará más código y el servidor parará
			if(!linea.equalsIgnoreCase("FIN")) {
				respuesta = canal.comprobarBoleto(linea);
				canal.enviarRespuesta(respuesta);
			}
		} while (!linea.equalsIgnoreCase("FIN"));
		canal.finSesion();
	}

}
