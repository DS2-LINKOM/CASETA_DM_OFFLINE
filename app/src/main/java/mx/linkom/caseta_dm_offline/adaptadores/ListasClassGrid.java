package mx.linkom.caseta_dm_offline.adaptadores;

/**
 * Created by one on 19/8/16.
 */
public class ListasClassGrid {

    private String title;
    private String subtitle;


    public ListasClassGrid(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

}
