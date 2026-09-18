package br.goiasrp.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.BLACK);

        TextView texto = new TextView(this);
        texto.setText("GOIÁS ROLEPLAY\n\nBase do jogo aguardando instalação.");
        texto.setTextColor(Color.WHITE);
        texto.setTextSize(20);
        texto.setGravity(Gravity.CENTER);

        layout.addView(texto);

        setContentView(layout);
    }
}
