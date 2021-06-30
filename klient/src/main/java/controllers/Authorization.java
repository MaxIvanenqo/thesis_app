package controllers;

import client.App;
import client.Client;
import client.Global;
import client.HardwareDetect;
import client.utils.ArrayKit;
import client.utils.DialogUtil;
import client.utils.IntBytesConverter;
import client.utils.PrivateFolderGen;
import client.utils.crypto.RSA;
import client.utils.crypto.RSAGenerateKeys;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Authorization {
    @FXML
    public VBox vBox;
    @FXML
    public AnchorPane loginRootPane;

    @FXML
    public void initialize(){
        this.createCommonPart();
        this.createLoginBox();
    }

    private void createCommonPart(){
        ImageView logo = new ImageView();
        logo.setImage(new Image(String.valueOf(App.class.getResource("img/logo.png"))));
        logo.setFitWidth(140);
        logo.setFitHeight(100);
        Button stopAndClose = new Button();
            stopAndClose.getStyleClass().add("close-app-btn");
            stopAndClose.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            stopAndClose.setOnAction((ActionEvent evt)-> this.close());
            stopAndClose.setPrefWidth(30.0);
            stopAndClose.setPrefHeight(30.0);
            stopAndClose.setCursor(Cursor.HAND);
        Button minimizeWindow = new Button();
            minimizeWindow.getStyleClass().add("minimize-window-btn");
            minimizeWindow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            minimizeWindow.setOnAction((ActionEvent evt)-> this.minimize());
            minimizeWindow.setPrefWidth(30.0);
            minimizeWindow.setPrefHeight(30.0);
            minimizeWindow.setCursor(Cursor.HAND);
        HBox controlBtns = new HBox();
        controlBtns.setSpacing(5.0);
        controlBtns.getChildren().addAll(minimizeWindow, stopAndClose);
        controlBtns.setPrefWidth(100.0);
        controlBtns.setLayoutX(533.0);
        controlBtns.setLayoutY(2.0);
        this.loginRootPane.getChildren().addAll(logo, controlBtns);
    }

    public void createLoginBox(){
        this.vBox.getChildren().clear();
        Label title = new Label("Zaloguj się");
        title.getStyleClass().add("title");
        Label emailLabel = new Label("Email");
            emailLabel.getStyleClass().add("label");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Email podany przy rejestracji");
            emailInput.getStyleClass().add("input");
        Label passwordLabel = new Label("Hasło");
            passwordLabel.getStyleClass().add("label");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Hasło użytkownika");
            passwordInput.getStyleClass().add("input");
        HBox authBtnBox = new HBox();
        authBtnBox.setSpacing(10.0);
        authBtnBox.getStyleClass().add("auth_btn_container");
        Button buttonLogin = new Button("Zaloguj się");
            buttonLogin.getStyleClass().add("auth_btn_step");
        buttonLogin.setOnAction((ActionEvent evt)-> this.loginToApplication(emailInput.getText().trim(), passwordInput.getText().trim()));
        Button buttonSignUpOffer = new Button("Stwórz konto");
            buttonSignUpOffer.getStyleClass().add("auth_btn_other_option");
        buttonSignUpOffer.setOnAction((ActionEvent evt)-> this.createSignUpBox());
        authBtnBox.getChildren().addAll(buttonLogin, buttonSignUpOffer);
        this.vBox.getChildren().addAll(
                title,
                emailLabel,
                emailInput,
                passwordLabel,
                passwordInput,
                authBtnBox
        );
    }

    public void createSignUpBox(){
        Label title = new Label("Stwórz konto");
        title.getStyleClass().add("title");
        this.vBox.getChildren().clear();
        Label emailSignUpLabel = new Label("email");
            emailSignUpLabel.getStyleClass().add("label");
        TextField emailInput = new TextField();
            emailInput.getStyleClass().add("input");
            emailInput.setPromptText("Email adres");
        Label fullNameLabel = new Label("Imię nazwisko");
            fullNameLabel.getStyleClass().add("label");
        TextField fullNameInput = new TextField();
            fullNameInput.getStyleClass().add("input");
            fullNameInput.setPromptText("Imię nazwisko dla rejestracji");
        Label password1Label = new Label("Hasło");
            password1Label.getStyleClass().add("label");
        PasswordField password1Input = new PasswordField();
            password1Input.getStyleClass().add("input");
        Label password2Label = new Label("Powtórz hasło");
            password2Label.getStyleClass().add("label");
        PasswordField password2Input = new PasswordField();
            password2Input.getStyleClass().add("input");
        Button buttonSignUp = new Button("Stwórz konto");
            buttonSignUp.getStyleClass().add("auth_btn_step");
        buttonSignUp.setOnAction((ActionEvent evt)-> this.signUpToApplication(emailInput.getText().trim(), password1Input.getText().trim(), password2Input.getText().trim(), fullNameInput.getText().trim()));
        Button buttonBackToLogin = new Button("Zaloguj się");
        buttonBackToLogin.setOnAction((ActionEvent evt)-> this.createLoginBox());
            buttonBackToLogin.getStyleClass().add("auth_btn_other_option");
        HBox authBtnBox = new HBox();
        authBtnBox.getChildren().addAll(buttonSignUp, buttonBackToLogin);
            authBtnBox.getStyleClass().add("auth_btn_container");
            authBtnBox.setSpacing(10.0);
        this.vBox.getChildren().addAll(
                title,
                emailSignUpLabel,
                emailInput,
                fullNameLabel,
                fullNameInput,
                password1Label,
                password1Input,
                password2Label,
                password2Input,
                authBtnBox
        );
    }

    public void createConfirmationBox(String email){
        this.vBox.getChildren().clear();
        Label info1 = new Label();
        Label info2 = new Label();
        Label info3 = new Label();
            info1.setText("Na email, podany przy rejestracji");
            info2.setText("("+email+")");
            info3.setText("został wysłany kod potwierdzenia rejestracji. Sprawdź srkrzynkę pocztową i wprowadź kod w formularz");
            info2.getStyleClass().addAll("label", "strong", "highlighted");
            info3.setWrapText(true);
            info1.setPrefWidth(this.vBox.getPrefWidth());
            info2.setPrefWidth(this.vBox.getPrefWidth());
            info3.setPrefWidth(this.vBox.getPrefWidth());
        VBox infoBox = new VBox();
        infoBox.getStyleClass().add("info-box");
        infoBox.getChildren().addAll(info1, info2, info3);

        TextField pinInput = new TextField();
            pinInput.setPromptText("XXXXXX");
            pinInput.getStyleClass().add("input");
            pinInput.setAlignment(Pos.CENTER);

            Button sendConfirm = new Button("Potwierdź");
        sendConfirm.setOnAction((ActionEvent evt)->{
            if (pinInput.getText().equals("") || pinInput.getText().length()!=6) {
                DialogUtil.dialogError("Kod potwierdzenia ma nepoprawny format");
                return;
            }
            this.confirm(pinInput.getText());
        });
        Button cancel = new Button("Zmień dane");
        cancel.setOnAction((ActionEvent evt)-> this.createSignUpBox());
        HBox authBtnBox = new HBox();
            authBtnBox.getChildren().addAll(sendConfirm, cancel);
            authBtnBox.getStyleClass().add("auth_btn_container");
            authBtnBox.setSpacing(10.0);
        sendConfirm.getStyleClass().add("auth_btn_step");
        cancel.getStyleClass().add("auth_btn_other_option");

        vBox.getChildren().addAll(
                infoBox,
                pinInput,
                authBtnBox
        );
    }

    public void loginToApplication(String email, String password) {
        String systemInfo = HardwareDetect.getHardware();
        if (email.equals("")){
            DialogUtil.dialogError("Wprowadź imię użytkownika");
        }

        if (email.equals("")){
            DialogUtil.dialogError("Wprowadź imię użytkownika");
        }
        String username = email.split("@")[0]+"_"+email.split("@")[1];
        username = username.replace(".","");
        Global.setUsername(username);

        try {
            Global.password = MessageDigest.getInstance("SHA-256").digest(password.getBytes());
            if (RSA.readPrivateKeyFromFile(PrivateFolderGen.key(Global.getUsername(), "private")) == null){
                DialogUtil.dialogError("Błędne hasło");
                return;
            }
        } catch (NoSuchAlgorithmException e) {
            DialogUtil.dialogError("Błędne hasło!");
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(PrivateFolderGen.name());
            ObjectInputStream ois = new ObjectInputStream(fis);
            byte[] device_id  = ois.readAllBytes();
            ois.close();
            fis.close();
            Platform.runLater(()-> Client.getInstance().logenter(
                    ArrayKit.joinArrays(
                            IntBytesConverter.convertToBytes(device_id.length),
                            device_id,
                            IntBytesConverter.convertToBytes(email.getBytes().length),
                            email.getBytes(),
                            IntBytesConverter.convertToBytes(systemInfo.getBytes().length),
                            systemInfo.getBytes()
                    )
            ));
        }
        catch (IOException e){
            DialogUtil.dialogError("Sprawdź podane dane");
        }
    }

    private boolean passwordValidation(String password){
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}";
        return password.matches(pattern);
    }

    private boolean passwordCompare(String pass1, String pass2){
        return pass1.equals(pass2);
    }

    public void signUpToApplication(String email, String password1, String password2, String fullName) {
        String systemInfo = HardwareDetect.getHardware();
        if (!this.passwordValidation(password1)){
            DialogUtil.dialogError("Wprowadzone hasło nie spełnia warunki walidacji:" +
                    "\n" +
                    "- Musi zawierać conajmniej 8 znaków \n" +
                    "- Mieć co najmniej jedną dużą i jedną małą literę \n" +
                    "- Mieć co najmniej jedną cyfrę i jeden znak specjalny \n");
            return;
        }
        if (!this.passwordCompare(password1, password2)){
            DialogUtil.dialogError("Wprowadzone hasła nie są takie same");
            return;
        }
        byte[] passwordForPrivateKeyProtection = new byte[0];
        try {
            passwordForPrivateKeyProtection = MessageDigest.getInstance("SHA-256").digest(password1.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("error. Password hash generator");
        }
        String username = email.split("@")[0]+"_"+email.split("@")[1];
        username = username.replace(".","");
        System.out.println(username);
        Global.setUsername(username);
        RSAGenerateKeys.Generator(passwordForPrivateKeyProtection);
        String pubKey = RSA.getHexOfPublicKey();

        try (FileOutputStream fos = new FileOutputStream(PrivateFolderGen.username());
             ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            oos.writeUTF(fullName);
        } catch (Exception e) {
            DialogUtil.dialogError("Nie mogę stworzyć pliku " + PrivateFolderGen.username());
            return;
        }

        byte[] sign = ArrayKit.joinArrays(
                IntBytesConverter.convertToBytes(systemInfo.getBytes().length),
                systemInfo.getBytes(),
                IntBytesConverter.convertToBytes(email.getBytes().length),
                email.getBytes(),
                IntBytesConverter.convertToBytes(fullName.getBytes().length),
                fullName.getBytes(),
                IntBytesConverter.convertToBytes(pubKey.getBytes().length),
                pubKey.getBytes()
        );
        Client.getInstance().signingUp(sign);
        createConfirmationBox(email);
    }

    public void confirm(String pinCode) {
        Client.getInstance().sendSigningConfirmation(pinCode.getBytes());
    }

    public void close() {
        Client.getInstance().onDisconnect();
        Platform.exit();
    }

    public void minimize() {
        Stage stage = (Stage) this.vBox.getScene().getWindow();
        stage.setIconified(true);
    }
}
