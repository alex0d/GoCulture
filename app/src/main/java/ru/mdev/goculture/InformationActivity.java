package ru.mdev.goculture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView appDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appDescription = findViewById(R.id.app_description);
        String desc = getText(R.string.about_app_description).toString();

        appDescription.setMovementMethod(LinkMovementMethod.getInstance());
        appDescription.setText(HtmlCompat.fromHtml(desc, HtmlCompat.FROM_HTML_MODE_LEGACY));
    }
}
