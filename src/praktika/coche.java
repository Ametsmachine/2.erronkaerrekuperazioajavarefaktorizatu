package praktika;

public class coche {
	private String modelo;
    private String marca;
    private boolean disponible;

    public coche(String modelo, String marca) {
        this.modelo = modelo;
        this.marca = marca;
        this.disponible = true;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void alquilar() {
        this.disponible = false;
    }

    public void devolver() {
        this.disponible = true;
    }
}
}
