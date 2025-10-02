class Main {
    public static void main(String[] args) {
        Enigma enigma = Enigma.returnModel(Enigma.Model.M3);
        enigma.setRotors();
        enigma.setReflector();
        
    }
}