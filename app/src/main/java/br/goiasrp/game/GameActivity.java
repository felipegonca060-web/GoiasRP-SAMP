package br.goiasrp.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout tela = new FrameLayout(this);
        tela.setBackgroundColor(Color.rgb(25, 90, 55));

        TextView titulo = new TextView(this);
        titulo.setText("GOIÁS ROLEPLAY");
        titulo.setTextColor(Color.WHITE);
        titulo.setTextSize(32);
        titulo.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams tituloParams =
                new FrameLayout.LayoutParams(
                        -1,
                        120
                );

        tituloParams.gravity = Gravity.TOP;
        tituloParams.topMargin = 80;

        tela.addView(titulo, tituloParams);

        TextView cidade = new TextView(this);
        cidade.setText("CIDADE DE GOIÁS");
        cidade.setTextColor(Color.WHITE);
        cidade.setTextSize(20);
        cidade.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams cidadeParams =
                new FrameLayout.LayoutParams(
                        -1,
                        80
                );

        cidadeParams.gravity = Gravity.CENTER;
        tela.addView(cidade, cidadeParams);

        Button jogar = new Button(this);
        jogar.setText("▶ JOGAR");
        jogar.setTextSize(20);
        jogar.setAllCaps(false);

        FrameLayout.LayoutParams jogarParams =
                new FrameLayout.LayoutParams(
                        500,
                        80
                );

        jogarParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        jogarParams.bottomMargin = 100;

        tela.addView(jogar, jogarParams);

        jogar.setOnClickListener(v -> {
            cidade.setText(
                    "PERSONAGEM CRIADO!\n\n" +
                    "Bem-vindo ao Goiás RolePlay"
            );
        });

        setContentView(tela);
    }
}
