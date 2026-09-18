package br.goiasrp.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class GameActivity extends Activity {

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

        layout.addView(titulo,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

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
        status.setPadding(10, 30, 10, 30);

        layout.addView(status);

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
                "Base do jogo\n" +
                "Versão 1.0"
        );
        info.setTextColor(Color.GRAY);
        info.setTextSize(13);
        info.setGravity(Gravity.CENTER);

        layout.addView(info);

        setContentView(layout);
    }

    private void verificarArquivos() {

        File pasta = getExternalFilesDir(null);

        if (pasta == null) {
            status.setText("❌ Não foi possível acessar o armazenamento.");
            return;
        }

        File data = new File(pasta, "data");
        File models = new File(pasta, "models");
        File samp = new File(pasta, "SAMP");
        File texdb = new File(pasta, "texdb");

        boolean temData = data.isDirectory();
        boolean temModels = models.isDirectory();
        boolean temSamp = samp.isDirectory();
        boolean temTexdb = texdb.isDirectory();

        if (temData && temModels && temSamp && temTexdb) {

            status.setText(
                    "✅ ARQUIVOS ENCONTRADOS\n\n" +
                    "data ✓\n" +
                    "models ✓\n" +
                    "SAMP ✓\n" +
                    "texdb ✓"
            );

        } else {

            StringBuilder resultado = new StringBuilder();

            resultado.append("⚠️ ARQUIVOS INCOMPLETOS\n\n");

            resultado.append("data ")
                    .append(temData ? "✓" : "✗")
                    .append("\n");

            resultado.append("models ")
                    .append(temModels ? "✓" : "✗")
                    .append("\n");

            resultado.append("SAMP ")
                    .append(temSamp ? "✓" : "✗")
                    .append("\n");

            resultado.append("texdb ")
                    .append(temTexdb ? "✓" : "✗");

            status.setText(resultado.toString());
        }
    }

    private void iniciarJogo() {

        Toast.makeText(
                this,
                "A base do jogo ainda precisa ser integrada.",
                Toast.LENGTH_LONG
        ).show();
    }
}
