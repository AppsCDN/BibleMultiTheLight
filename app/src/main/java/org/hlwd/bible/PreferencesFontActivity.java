
package org.hlwd.bible;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class PreferencesFontActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final int themeId = PCommon.GetPrefThemeId(this);
        setTheme(themeId);
        ShowTypefaces();
    }

    private void ShowTypefaces()
    {
        try
        {
            final Context context = this;
            final LayoutInflater inflater = getLayoutInflater();
            final LinearLayout llFont = (LinearLayout) (inflater.inflate(R.layout.activity_font_preferences, (LinearLayout) findViewById(R.id.llFont)));
            final RadioGroup radioGroup = new RadioGroup(context);
            final String fontNameSelected = PCommon.GetPref(context, IProject.APP_PREF_KEY.FONT_NAME);
            final ScrollView svFont = new ScrollView(context);
            llFont.addView(radioGroup);
            svFont.addView(llFont);

            int index = 0;
            RadioButton radioFont;
            TextView tvEx;
            Typeface fontEx;

            for (String fontName : context.getResources().getStringArray(R.array.FONT_VALUES_ARRAY))
            {
                fontEx = (fontName == null || fontName.length() == 0)
                        ? Typeface.defaultFromStyle(Typeface.NORMAL)
                        : Typeface.createFromAsset(context.getAssets(), PCommon.ConcaT("fonts/", fontName, ".ttf"));

                radioFont = new RadioButton(context);
                radioFont.setChecked( fontNameSelected.equalsIgnoreCase( fontName ));
                radioFont.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                radioFont.setText((fontName == null || fontName.length() == 0) ? getString(R.string.mnuTypefaceDefault) : fontName );
                radioFont.setTag( fontName );
                radioFont.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View view)
                    {
                        try
                        {
                            final String fontName = (String) view.getTag();
                            PCommon.SavePref(view.getContext(), IProject.APP_PREF_KEY.FONT_NAME, fontName);
                            finish();
                        }
                        catch (Exception ex)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(view.getContext(), ex);
                        }
                    }
                });

                tvEx = new TextView(context);
                tvEx.setLayoutParams(PCommon._layoutParamsMatchAndWrap);
                tvEx.setText( R.string.tvFontExample );
                tvEx.setTag( R.id.tv1, index );
                tvEx.setTag( R.id.tv2, fontName );
                tvEx.setTypeface(fontEx);                                                           //Font
                tvEx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view)
                    {
                        final int index = (int) view.getTag(R.id.tv1);
                        final String fontName = (String) view.getTag(R.id.tv2);

                        ((RadioButton)(radioGroup.getChildAt(index))).setChecked(true);
                        PCommon.SavePref(view.getContext(), IProject.APP_PREF_KEY.FONT_NAME, fontName);
                        finish();
                    }
                });

                radioGroup.addView(radioFont);
                radioGroup.addView(tvEx);

                index = index + 2;
            }

            setContentView(svFont);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(getApplicationContext(), ex);
        }
    }
}
