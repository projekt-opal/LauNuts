package org.dice_research.opal.launuts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serialization of objects.
 * 
 * Based on https://www.baeldung.com/java-serialization
 * 
 * @author Adrian Wilke
 */
public abstract class Serialization {

	public static void write(Object object, File file) throws IOException {
		file.getParentFile().mkdirs();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.flush();
		objectOutputStream.close();
	}

	public static Object read(File file) throws IOException, ClassNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(file);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		return object;
	}
}