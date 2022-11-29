package mx.linkom.caseta_dm_offline.adaptadores;

/**
 * Created by one on 19/8/16.
 */
public class ListasHoraFechaClassGrid {

    private String title;
    private String subtitle;
    private String fecha;


    public ListasHoraFechaClassGrid(String title, String fecha, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.fecha = fecha ;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFecha() {return fecha;}

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.title = subtitle;
    }

}
