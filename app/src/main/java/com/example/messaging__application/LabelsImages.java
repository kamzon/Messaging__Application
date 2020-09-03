package com.example.messaging__application;

public class LabelsImages {

    private String imagePath;
    private String imageClasse;


    public LabelsImages() {
    }


    public LabelsImages(String imagePath, String imageClasse) {
        this.imagePath = imagePath;
        this.imageClasse = imageClasse;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageClasse() {
        return imageClasse;
    }


    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImageClasse(String imageClasse) {
        this.imageClasse = imageClasse;
    }
}
