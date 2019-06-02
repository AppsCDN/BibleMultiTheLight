package org.hlwd.bible;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TtsManager
{
    private Context context = null;
    private boolean isLoaded = false;
    private TextToSpeech tts = null;
    private final long SLEEP_WHEN_SPEAKING_MILLICS = 1000;
    private final long SLEEP_WHEN_NOT_READY_MILLICS = 300;

    TtsManager(final Context ctx, final Locale locale)
    {
        try
        {
            isLoaded = false;
            tts = null;
            context = ctx;

            final TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener()
            {
                @Override
                public void onInit(int status)
                {
                    try
                    {
                        if (status == TextToSpeech.SUCCESS)
                        {
                            final int result = tts.setLanguage(locale);
                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                            {
                                //TTS language is missing or not supported!
                            }
                            else
                            {
                                isLoaded = true;
                            }
                        }
                        else
                        {
                           //TTS initialization failed!
                        }
                    }
                    catch (Exception ex)
                    {
                        if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
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
     * @return True if was loaded before the limit time
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

    public void ShutDown()
    {
        //Rem: don't thread.sleep here, impact UI
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
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
        finally
        {
            tts = null;
            isLoaded = false;
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

                tts.speak(msg, TextToSpeech.QUEUE_ADD,null);
            }
            else
            {
                throw new Exception("TTS not loaded!");
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