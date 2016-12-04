package Application.Models;

import Application.App;

import javax.imageio.ImageIO;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

/**
 * Created by Yeso on 04/12/2016.
 */
public class css {
    String name;
    String path = "/css/";
    String fileName;

    public css(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return name;
    }

    public void copy(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    public void copy2(){

    }
}
