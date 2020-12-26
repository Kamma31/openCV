package main.misc;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Tasks {
    NULL("", null),
    SIGNAL_PROCESSING("---- Signal processing ----", null),
    FACE_DETECTION("Face detection", "Simple détection de visage sur une photo"),
    LINEAR_BLEND("Linear add", "Fusion linéaire de deux images img = (1−α).src1 + α.src2"),
    CONTRAST("Contrast and brightness", """
            Traitement d'image en jouant sur le contraste et la luminosité.
            On peut aussi observer l'impact de la correction de gamme.
            """),
    SMOOTHING("Smoothing", """
            Différentes méthodes de floutage d'image.
            (normalisé, gaussienne, médiane, bilateral)
            """),
    TRESHOLDING("Tresholding", "Différents types de seuillage d'image"),

    NULLC("",null),
    MORPHOLOGY("---- Morphology ----", null),
    ERODE_DILATE("Erode and dilate", """
            Deux opérations qui traite l'image à partir de formes.
            Le morphisme applique un élément structurel.
            Champs d'application: suppression du bruit, isolation ou regroupement 
            d'éléments, recherche de "bosses" ou "creux" dans une image.
            """),
    OTHERS("Morphology","D'autres opération morphologiques disponibles"),

    NULLA("  ", null),
    THEORICAL("---- Theorical ----", null),
    MAT_MASK("Mat Mask", """
            Permet l'application d'un masque sur une image données.
            L'utilisation d'OpenCv permet d'optimiser le traitement.
            """),
    DFT("Discrete Fourier Transform", """
            La transformée de Fourier permet une transformation d'un signal / d'une image 
            du domaine spatial vers le domaine temporel. Une application peut par exemple 
            être la détermination de l'orientation des composant d'une image.
            """),

    NULLB(" ", null),
    TEST("Test", "Juste une classe de test pour s'assurer que la librairie est correctement chargée.");

    private final String name;
    private final String infos;

    public boolean isClickableTask(){
        return getInfos() != null;
    }
}
