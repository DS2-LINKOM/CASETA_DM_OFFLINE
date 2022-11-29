package mx.linkom.caseta_dm_offline.Model;

public class CasetaModel {
    public String name,image,descripcion;

    public CasetaModel(String name, String image, String descripcion) {
        this.name = name;
        this.image = image;
        this.descripcion = descripcion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
