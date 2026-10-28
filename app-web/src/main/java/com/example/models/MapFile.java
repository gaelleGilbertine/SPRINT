package com.example.models;

/**
 * Pièce jointe d'un dossier : le nom et le type du fichier, plus les deux
 * faces d'une CIN stockées en binaire.
 */
public class MapFile {

    private String nomFichier;
    private String type;
    private byte[] rectoCIN;
    private byte[] versoCIN;

    public MapFile() {}

    public String getNomFichier() { return nomFichier; }
    public void setNomFichier(String nomFichier) { this.nomFichier = nomFichier; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public byte[] getRectoCIN() { return rectoCIN; }
    public void setRectoCIN(byte[] rectoCIN) { this.rectoCIN = rectoCIN; }

    public byte[] getVersoCIN() { return versoCIN; }
    public void setVersoCIN(byte[] versoCIN) { this.versoCIN = versoCIN; }
}
