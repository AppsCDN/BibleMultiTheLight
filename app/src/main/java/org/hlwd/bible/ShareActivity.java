
package org.hlwd.bible;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//TODO: bug: don't scroll
//TODO: tab title too long
public class ShareActivity extends AppCompatActivity
{
    private SCommon _s = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        CheckLocalInstance(getApplicationContext());

        final int themeId = PCommon.GetPrefThemeId(getApplicationContext());
        setTheme(themeId);
        ShareText();
    }

    private void CheckLocalInstance(final Context context)
    {
        try
        {
            if (_s == null)
            {
                _s = SCommon.GetInstance(context);
            }
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    private void ShareText()
    {
        try
        {
            final Intent intent = getIntent();
            final String action = intent.getAction();
            final String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null)
            {
                if ("text/plain".equalsIgnoreCase(type))
                {
                    final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    setContentView(R.layout.activity_share);
                    final EditText etSharedText = (EditText) findViewById(R.id.etSharedText);
                    etSharedText.setText(sharedText);

                    final String bbName = PCommon.GetPrefBibleName(getApplicationContext());
                    final String bbNameLanguage = (bbName.compareToIgnoreCase("k") == 0) ? "EN" : (bbName.compareToIgnoreCase("d") == 0) ? "IT" : (bbName.compareToIgnoreCase("v") == 0) ? "ES" : (bbName.compareToIgnoreCase("l") == 0) ? "FR" : "EN";
                    final Button btnLanguage = (Button) findViewById(R.id.btnLanguage);
                    btnLanguage.setText(bbNameLanguage);
                    btnLanguage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String bbNameLanguage = RollLanguageName(v.getContext(), btnLanguage.getText().toString());
                            btnLanguage.setText(bbNameLanguage);
                        }
                    });
                    final Button btnSelect = (Button) findViewById(R.id.btnSelect);
                    btnSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!etSharedText.hasSelection()) return;
                            final int selStart = etSharedText.getSelectionStart();
                            final int selEnd = etSharedText.getSelectionEnd();
                            etSharedText.setText(etSharedText.getText().toString().substring(selStart, selEnd));
                        }
                    });
                    final Button btnTrim = (Button) findViewById(R.id.btnTrim);
                    btnTrim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etSharedText.setText(etSharedText.getText().toString().trim());
                        }
                    });
                    final Button btnClose = (Button) findViewById(R.id.btnClose);
                    btnClose.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            finish();
                        }
                    });
                    final Button btnSearch = (Button) findViewById(R.id.btnSearch);
                    btnSearch.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            final Handler handler = new Handler();
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    etSharedText.setText(etSharedText.getText().toString().replaceAll("\n", ""));
                                    if (etSharedText.getText().length() >= 3)
                                    {
                                        final String bbNameLanguage = btnLanguage.getText().toString();
                                        final String bbName = bbNameLanguage.equalsIgnoreCase("EN") ? "k"
                                                : bbNameLanguage.equalsIgnoreCase("ES") ? "v"
                                                : bbNameLanguage.equalsIgnoreCase("FR") ? "l"
                                                : bbNameLanguage.equalsIgnoreCase("IT") ? "d"
                                                : "k";
                                        MainActivity.Tab.AddTab(getApplicationContext(), "I", bbName, etSharedText.getText().toString());
                                        finish();
                                    }
                                    else
                                    {
                                        PCommon.ShowToast(getApplicationContext(), R.string.toastEmpty, Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                        }
                    });
                }
                else
                {
                    finish();
                }
            }
            else
            {
                finish();
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
            //noinspection EmptyFinallyBlock
            try { finish(); } finally {}
        }
    }

    private String RollLanguageName(final Context context, final String bbLanguageName)
    {
        try
        {
            final int INSTALL_STATUS = _s.GetInstallStatus(context);
            switch (INSTALL_STATUS)
            {
                case 1:
                {
                    return "EN";
                }
                case 2:
                {
                    return (bbLanguageName.compareToIgnoreCase("EN") == 0) ? "ES" : "EN";
                }
                case 3:
                {
                    return (bbLanguageName.compareToIgnoreCase("EN") == 0) ? "ES" : (bbLanguageName.compareToIgnoreCase("ES") == 0) ? "FR" : "EN";
                }
                case 4:
                {
                    return (bbLanguageName.compareToIgnoreCase("EN") == 0) ? "ES" : (bbLanguageName.compareToIgnoreCase("ES") == 0) ? "FR" : (bbLanguageName.compareToIgnoreCase("FR") == 0) ? "IT" : "EN";
                }
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return "EN";
    }
}
