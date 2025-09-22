package br.com.egressos.domain;

import java.net.URL;
import java.util.Objects;

public class RedeSocial {
    private TipoRede tipo;
    private URL url;

    public RedeSocial() {}
    public RedeSocial(TipoRede tipo, URL url) { this.tipo = tipo; this.url = url; }

    public TipoRede getTipo() { return tipo; }
    public void setTipo(TipoRede tipo) { this.tipo = tipo; }
    public URL getUrl() { return url; }
    public void setUrl(URL url) { this.url = url; }

    @Override public String toString() { return tipo + ":" + url; }
    @Override public boolean equals(Object o){ 
        if(this==o) return true; 
        if(!(o instanceof RedeSocial rs)) return false;
        return tipo==rs.tipo && Objects.equals(url, rs.url);
    }
    @Override public int hashCode(){ return Objects.hash(tipo, url); }
}
