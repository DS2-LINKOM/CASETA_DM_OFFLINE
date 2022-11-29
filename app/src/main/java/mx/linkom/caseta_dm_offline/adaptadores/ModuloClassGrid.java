package mx.linkom.caseta_dm_offline.adaptadores;

public class ModuloClassGrid {

    private int add;
    private String title;
    private String colorCode;


    public ModuloClassGrid(int add, String title, String colorCode) {
        this.add = add;
        this.title = title;
        this.colorCode = colorCode;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getImagen() {
        return add;
    }

    public void setImagen(int add) {
        this.add = add;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

}
