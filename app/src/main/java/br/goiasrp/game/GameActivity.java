package br.goiasrp.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.database.Cursor;

public class GameActivity extends Activity {

    private static final int REQUEST_IMPORT_FOLDER = 1001;

    private LinearLayout layout;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        criarTela();
        verificarArquivos();
    }

    private void criarTela() {

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(35, 35, 35, 35);
        layout.setBackgroundColor(Color.rgb(7, 17, 31));

        TextView titulo = new TextView(this);
        titulo.setText("GOIÁS ROLEPLAY");
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(30);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(10, 20, 10, 20);

        layout.addView(titulo);

        TextView subtitulo = new TextView(this);
        subtitulo.setText("BASE DO JOGO");
        subtitulo.setTextColor(Color.LTGRAY);
        subtitulo.setTextSize(16);
        subtitulo.setGravity(Gravity.CENTER);

        layout.addView(subtitulo);

        status = new TextView(this);
        status.setText("Verificando arquivos...");
        status.setTextColor(Color.WHITE);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(10, 25, 10, 25);

        layout.addView(status);

        Button importar = new Button(this);
        importar.setText("📂 IMPORTAR ARQUIVOS DO JOGO");
        importar.setTextSize(16);
        importar.setAllCaps(false);

        layout.addView(importar,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        70
                ));

        importar.setOnClickListener(v -> escolherPasta());

        Button verificar = new Button(this);
        verificar.setText("🔄 VERIFICAR ARQUIVOS");
        verificar.setTextSize(16);
        verificar.setAllCaps(false);

        layout.addView(verificar,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        65
                ));

        verificar.setOnClickListener(v -> verificarArquivos());

        Button jogar = new Button(this);
        jogar.setText("▶ JOGAR");
        jogar.setTextSize(18);
        jogar.setAllCaps(false);

        layout.addView(jogar,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        70
                ));

        jogar.setOnClickListener(v -> iniciarJogo());

        TextView info = new TextView(this);
        info.setText(
                "\nGoiás RolePlay\n" +
                "Gerenciador de arquivos\n" +
                "Versão 1.0"
        );

        info.setTextColor(Color.GRAY);
        info.setTextSize(13);
        info.setGravity(Gravity.CENTER);

        layout.addView(info);

        setContentView(layout);
    }

    /*
     * Abre o seletor oficial de pastas do Android.
     */
    private void escolherPasta() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        );

        startActivityForResult(intent, REQUEST_IMPORT_FOLDER);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_IMPORT_FOLDER) {
            return;
        }

        if (resultCode != RESULT_OK || data == null) {

            Toast.makeText(
                    this,
                    "Importação cancelada.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Uri treeUri = data.getData();

        if (treeUri == null) {
            return;
        }

        try {

            getContentResolver().takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

        } catch (Exception ignored) {
        }

        importarPasta(treeUri);
    }

    /*
     * Copia a pasta selecionada para a área própria do APK 2.
     */
    private void importarPasta(Uri treeUri) {

        status.setText(
                "⏳ IMPORTANDO ARQUIVOS...\n\n" +
                "Não feche o aplicativo."
        );

        new Thread(() -> {

            try {

                File destino = getGameFolder();

                if (!destino.exists()) {
                    destino.mkdirs();
                }

                copiarDiretorio(treeUri, destino);

                runOnUiThread(() -> {

                    status.setText(
                            "✅ IMPORTAÇÃO CONCLUÍDA\n\n" +
                            "Os arquivos foram copiados."
                    );

                    Toast.makeText(
                            GameActivity.this,
                            "Arquivos importados com sucesso!",
                            Toast.LENGTH_LONG
                    ).show();

                    verificarArquivos();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    status.setText(
                            "❌ ERRO NA IMPORTAÇÃO\n\n" +
                            e.getMessage()
                    );

                    Toast.makeText(
                            GameActivity.this,
                            "Não foi possível importar os arquivos.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }

        }).start();
    }

    /*
     * Percorre a pasta escolhida e copia todos os arquivos.
     */
    private void copiarDiretorio(Uri treeUri, File destino)
            throws Exception {

        String documentId =
                DocumentsContract.getTreeDocumentId(treeUri);

        Uri childrenUri =
                DocumentsContract.buildChildDocumentsUriUsingTree(
                        treeUri,
                        documentId
                );

        Cursor cursor = getContentResolver().query(
                childrenUri,
                new String[]{
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE
                },
                null,
                null,
                null
        );

        if (cursor == null) {
            throw new Exception("Não foi possível ler a pasta.");
        }

        try {

            int idColumn = cursor.getColumnIndex(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID
            );

            int nameColumn = cursor.getColumnIndex(
                    DocumentsContract.Document.COLUMN_DISPLAY_NAME
            );

            int mimeColumn = cursor.getColumnIndex(
                    DocumentsContract.Document.COLUMN_MIME_TYPE
            );

            while (cursor.moveToNext()) {

                String id = cursor.getString(idColumn);
                String nome = cursor.getString(nameColumn);
                String mime = cursor.getString(mimeColumn);

                Uri arquivoUri =
                        DocumentsContract.buildDocumentUriUsingTree(
                                treeUri,
                                id
                        );

                File arquivoDestino =
                        new File(destino, nome);

                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {

                    if (!arquivoDestino.exists()) {
                        arquivoDestino.mkdirs();
                    }

                    copiarDiretorio(
                            arquivoUri,
                            arquivoDestino
                    );

                } else {

                    copiarArquivo(
                            arquivoUri,
                            arquivoDestino
                    );
                }
            }

        } finally {

            cursor.close();
        }
    }

    /*
     * Copia um arquivo usando ContentResolver.
     */
    private void copiarArquivo(
            Uri origem,
            File destino) throws Exception {

        InputStream input =
                getContentResolver().openInputStream(origem);

        if (input == null) {
            throw new Exception(
                    "Não foi possível abrir: " + destino.getName()
            );
        }

        if (destino.getParentFile() != null) {
            destino.getParentFile().mkdirs();
        }

        OutputStream output =
                new java.io.FileOutputStream(destino);

        byte[] buffer = new byte[8192];

        int lidos;

        try {

            while ((lidos = input.read(buffer)) != -1) {
                output.write(buffer, 0, lidos);
            }

            output.flush();

        } finally {

            try {
                input.close();
            } catch (Exception ignored) {
            }

            try {
                output.close();
            } catch (Exception ignored) {
            }
        }
    }

    /*
     * Pasta interna/externa própria do APK 2.
     */
    private File getGameFolder() {

        File base = getExternalFilesDir(null);

        return new File(base, "game");
    }

    /*
     * Verifica a estrutura do Goiás RP.
     */
    private void verificarArquivos() {

        File root = getGameFolder();

        File data = new File(root, "data");
        File models = new File(root, "models");
        File samp = new File(root, "SAMP");
        File texdb = new File(root, "texdb");

        boolean temData = data.isDirectory();
        boolean temModels = models.isDirectory();
        boolean temSamp = samp.isDirectory();
        boolean temTexdb = texdb.isDirectory();

        if (temData && temModels && temSamp && temTexdb) {

            status.setText(
                    "✅ ARQUIVOS COMPLETOS\n\n" +
                    "data ✓\n" +
                    "models ✓\n" +
                    "SAMP ✓\n" +
                    "texdb ✓"
            );

        } else {

            status.setText(
                    "⚠️ ARQUIVOS INCOMPLETOS\n\n" +
                    "data " + (temData ? "✓" : "✗") + "\n" +
                    "models " + (temModels ? "✓" : "✗") + "\n" +
                    "SAMP " + (temSamp ? "✓" : "✗") + "\n" +
                    "texdb " + (temTexdb ? "✓" : "✗")
            );
        }
    }

    /*
     * Por enquanto é apenas o ponto de entrada.
     * A base Android real será integrada aqui posteriormente.
     */
    private void iniciarJogo() {

        File root = getGameFolder();

        File data = new File(root, "data");
        File models = new File(root, "models");
        File samp = new File(root, "SAMP");
        File texdb = new File(root, "texdb");

        if (!data.isDirectory()
                || !models.isDirectory()
                || !samp.isDirectory()
                || !texdb.isDirectory()) {

            Toast.makeText(
                    this,
                    "Importe os arquivos do jogo primeiro.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Arquivos encontrados. A base do jogo ainda precisa ser integrada.",
                Toast.LENGTH_LONG
        ).show();
    }
}
