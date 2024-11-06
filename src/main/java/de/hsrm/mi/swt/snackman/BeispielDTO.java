package de.hsrm.mi.swt.snackman;
// record BeispielDTO(String anfragerip, String zeitstempel) {}

public class BeispielDTO {
    private String anfragerip;
    private String zeitstempel;

    public BeispielDTO() {
        this("", "");
    }
    public BeispielDTO(String anfragerip, String zeitstempel) {
        this.anfragerip = anfragerip;
        this.zeitstempel = zeitstempel;
    }
    public String getAnfragerip() {
        return anfragerip;
    }
    public void setAnfragerip(String anfragerip) {
        this.anfragerip = anfragerip;
    }
    public String getZeitstempel() {
        return zeitstempel;
    }
    public void setZeitstempel(String zeitstempel) {
        this.zeitstempel = zeitstempel;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((anfragerip == null) ? 0 : anfragerip.hashCode());
        result = prime * result + ((zeitstempel == null) ? 0 : zeitstempel.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BeispielDTO other = (BeispielDTO) obj;
        if (anfragerip == null) {
            if (other.anfragerip != null)
                return false;
        } else if (!anfragerip.equals(other.anfragerip))
            return false;
        if (zeitstempel == null) {
            if (other.zeitstempel != null)
                return false;
        } else if (!zeitstempel.equals(other.zeitstempel))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "BeispielDTO [anfragerip=" + anfragerip + ", zeitstempel=" + zeitstempel + "]";
    }
    
}
