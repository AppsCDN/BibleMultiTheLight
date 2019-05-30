package org.hlwd.bible;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

//TODO TTS: logs in debug
public class TtsManager
{
    private Context context = null;
    private Locale locale = null;
    private boolean isLoaded = false;
    private TextToSpeech tts = null;
    private final long SLEEP_WHEN_SPEAKING_MILLICS = 1000;
    private final long SLEEP_WHEN_NOT_READY_MILLICS = 300;

    TtsManager(final Context ctx, final Locale local)
    {
        try
        {
            isLoaded = false;
            tts = null;
            context = ctx;
            locale = local;

            final TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener()
            {
                @Override
                public void onInit(int status)
                {
                    if (status == TextToSpeech.SUCCESS)
                    {
                        int result = tts.setLanguage(locale);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        {
                            Log.e("TTS", "This Language is not supported");
                            return;
                        }

                        isLoaded = true;
                    }
                    else
                    {
                        Log.e("TTS", "Initilization Failed!");
                    }
                }
            };
            tts = new TextToSpeech(context, onInitListener);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    /***
     * Wait several seconds for TTS to be ready
     * @return True if was loaded the limit time
     */
    public boolean WaitForReady()
    {
        try
        {
            final int loopLimit = 10;

            int loopCount = 0;
            while (!IsLoaded() && loopCount < loopLimit)
            {
                Thread.sleep(SLEEP_WHEN_NOT_READY_MILLICS);

                loopCount++;
            }

            return (IsLoaded() && loopCount < loopLimit);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return false;
    }

    //TODO TTS (called by UI)
    public void ShutDown()
    {
        try
        {
            if (IsLoaded())
            {
                try
                {
                    tts.stop();
                }
                catch (Exception ex)
                { }

                try
                {
                    tts.shutdown();
                }
                catch (Exception ex)
                { }

                tts = null;
                isLoaded = false;
                locale = null;
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    public void SayClear(final String msg)
    {
        try
        {
            if (IsLoaded())
            {
                tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                Log.e("error", "TTS Not Initialized");
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    public void SayAdd(final String msg)
    {
        try
        {
            if (IsLoaded())
            {
                while (tts.isSpeaking())
                {
                    Thread.sleep(SLEEP_WHEN_SPEAKING_MILLICS);
                }

                tts.speak(msg, TextToSpeech.QUEUE_ADD, null);
            }
            else
            {
                Log.e("error", "TTS Not Initialized");
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }

    public boolean IsLoaded()
    {
        try
        {
            if (tts == null) return false;
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }

        return isLoaded;
    }
}