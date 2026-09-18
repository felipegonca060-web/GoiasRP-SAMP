package br.goiasrp.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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
                new LinearLayout.LayoutParams(-1, 70));

        importar.setOnClickListener(v -> escolherPasta());

        Button verificar = new Button(this);
        verificar.setText("🔄 VERIFICAR ARQUIVOS");
        verificar.setTextSize(16);
        verificar.setAllCaps(false);
        layout.addView(verificar,
                new LinearLayout.LayoutParams(-1, 65));

        verificar.setOnClickListener(v -> verificarArquivos());

        Button jogar = new Button(this);
        jogar.setText("▶ JOGAR");
        jogar.setTextSize(18);
        jogar.setAllCaps(false);
        layout.addView(jogar,
                new LinearLayout.LayoutParams(-1, 70));

        jogar.setOnClickListener(v -> iniciarJogo());

        TextView info = new TextView(this);
        info.setText("\nGoiás RolePlay\nGerenciador de arquivos\nVersão 1.0");
        info.setTextColor(Color.GRAY);
        info.setTextSize(13);
        info.setGravity(Gravity.CENTER);
        layout.addView(info);

        setContentView(layout);
    }

    private void escolherPasta() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        );

        startActivityForResult(intent, REQUEST_IMPORT_FOLDER);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_IMPORT_FOLDER ||
                resultCode != RESULT_OK ||
                data == null) {
            return;
        }

        Uri treeUri = data.getData();

        if (treeUri == null) {
            return;
        }

        try {
            getContentResolver().takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );
        } catch (Exception ignored) {
        }

        importarPasta(treeUri);
    }

    private void importarPasta(Uri treeUri) {

        status.setText(
                "⏳ IMPORTANDO...\n\n" +
                "Aguarde até terminar."
        );

        new Thread(() -> {

            try {

                File destino = getGameFolder();

                if (!destino.exists()) {
                    destino.mkdirs();
                }

                String documentId =
        DocumentsContract.getTreeDocumentId(treeUri);

copiarPasta(
        treeUri,
        documentId,
        destino
);

                runOnUiThread(() -> {

                    verificarArquivos();

                    Toast.makeText(
                            this,
                            "Importação concluída!",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    status.setText(
                            "❌ ERRO NA IMPORTAÇÃO\n\n" +
                            e.getMessage()
                    );

                    Toast.makeText(
                            this,
                            "Erro ao importar os arquivos.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }

        }).start();
    }

private void copiarPasta(
        Uri raizUri,
        String documentId,
        File destino) throws Exception {

    Uri childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(
                    raizUri,
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

            File destinoArquivo =
                    new File(destino, nome);

            Uri arquivoUri =
                    DocumentsContract.buildDocumentUriUsingTree(
                            raizUri,
                            id
                    );

            if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {

                if (!destinoArquivo.exists()) {
                    destinoArquivo.mkdirs();
                }

                copiarPasta(
                        raizUri,
                        id,
                        destinoArquivo
                );

            } else {

                copiarArquivo(
                        arquivoUri,
                        destinoArquivo
                );
            }
        }

    } finally {

        cursor.close();
    }
}

    private void copiarArquivo(
            Uri origem,
            File destino) throws Exception {

        InputStream input =
                getContentResolver().openInputStream(origem);

        if (input == null) {
            throw new Exception(
                    "Não foi possível abrir " +
                    destino.getName()
            );
        }

        if (destino.getParentFile() != null) {
            destino.getParentFile().mkdirs();
        }

        OutputStream output =
                new java.io.FileOutputStream(destino);

        byte[] buffer = new byte[8192];
        int quantidade;

        try {

            while ((quantidade = input.read(buffer)) != -1) {
                output.write(buffer, 0, quantidade);
            }

            output.flush();

        } finally {

            input.close();
            output.close();
        }
    }

    private File getGameFolder() {

        File base = getExternalFilesDir(null);

        return new File(base, "game");
    }

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

    private void iniciarJogo() {

        File root = getGameFolder();

        if (!new File(root, "data").isDirectory() ||
            !new File(root, "models").isDirectory() ||
            !new File(root, "SAMP").isDirectory() ||
            !new File(root, "texdb").isDirectory()) {

            Toast.makeText(
                    this,
                    "Importe todos os arquivos primeiro.",
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
