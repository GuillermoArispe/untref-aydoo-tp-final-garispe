package untref.aydoo.procesador;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.ho.yaml.Yaml;

import au.com.bytecode.opencsv.CSVReader;

public class ManejadorArchivos {

	File directorio;

	public void escribirYML(Resultado resultado) throws IOException {

		Yaml.dump(resultado, new File(directorio.getName() + "/resultado.yml"));
	}

	public void descomprimirZIP(File archivo) throws IOException, ZipException {

		ZipFile zip = new ZipFile(archivo);
		zip.extractAll(directorio.getPath());
		System.out.println("*** [Extraccion de Archivos finalizada] ***\n");
	}

	public List<RecorridoPorBicicleta> cargarRecorridos(File directorio)
			throws ZipException, IOException {

		this.directorio = directorio;

		File[] archivos;
		List<RecorridoPorBicicleta> recorridos = new ArrayList<RecorridoPorBicicleta>();

		archivos = directorio.listFiles();

		for (int i = 0; i < archivos.length; i++) {

			if (archivos[i].isFile() && archivos[i].getName().endsWith(".zip")) {

				recorridos.addAll(this.obtenerRecorridos(archivos[i]));
			}
		}

		return recorridos;
	}

	public List<RecorridoPorBicicleta> obtenerRecorridos(File archivoZip)
			throws IOException, ZipException {

		List<RecorridoPorBicicleta> recorridos = new ArrayList<RecorridoPorBicicleta>();
		File[] archivos;

		descomprimirZIP(archivoZip);

		archivos = directorio.listFiles();

		for (int i = 0; i < archivos.length; i++) {

			if (archivos[i].isFile() && archivos[i].getName().endsWith(".csv")) {

				recorridos.addAll(this.parsearCSV(archivos[i]));
			}
		}

		return recorridos;

	}

	public List<RecorridoPorBicicleta> parsearCSV(File archivo)
			throws IOException, ZipException {

		List<RecorridoPorBicicleta> recorridos = new ArrayList<RecorridoPorBicicleta>();

		CSVReader reader = new CSVReader(new FileReader(archivo), ';');

		// Salteo la primer fila
		reader.readNext();

		String[] linea;

		while ((linea = reader.readNext()) != null) {

			RecorridoPorBicicleta recorrido = new RecorridoPorBicicleta();

			try {

				recorrido.setID_usuario(Integer.parseInt(linea[0]));
				recorrido.setID_bicicleta(Integer.parseInt(linea[1]));

				recorrido.setFechaOrigen(linea[2]);
				recorrido.setID_estacionOrigen(Integer.parseInt(linea[3]));
				recorrido.setNombreOrigen(linea[4]);

				recorrido.setFechaDestino(linea[5]);
				recorrido.setID_estacionDestino(Integer.parseInt(linea[6]));
				recorrido.setNombreDestino(linea[7]);

				recorrido.setTiempoUso(Integer.parseInt(linea[8]));

				recorridos.add(recorrido);

				// Por lineas con errores en ultimo campo
			} catch (NumberFormatException e) {

				recorrido.setTiempoUso(0);
			}
		}

		reader.close();

		return recorridos;
	}

}