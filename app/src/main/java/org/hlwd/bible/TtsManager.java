package org.hlwd.bible;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

class TtsManager
{
    private Context context = null;
    private boolean isLoaded = false;
    private boolean isSpeaking = false;
    private TextToSpeech tts = null;
    private final long SLEEP_WHEN_SPEAKING_MILLICS = 1000;  //was 1000
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
                @SuppressWarnings("StatementWithEmptyBody")
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
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onStart(String utteranceId) {
                    isSpeaking = true;
                    //System.out.println("TTS progress: onStart");
                }

                @Override
                public void onDone(String utteranceId) {
                    //System.out.println("TTS progress: onDone");
                    isSpeaking = false;
                }

                @Override
                public void onError(String utteranceId) {
                    //System.out.println("TTS progress - onError");
                }
            });
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
    boolean WaitForReady()
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

    void ShutDown()
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
                catch (Exception ignored)
                { }

                try
                {
                    tts.shutdown();
                }
                catch (Exception ignored)
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

    void SayAdd(final String msg)
    {
        try
        {
            if (IsLoaded())
            {
                while (tts.isSpeaking())
                {
                    Thread.sleep(SLEEP_WHEN_SPEAKING_MILLICS);
                }

                Thread.sleep(3000);

                if(msg != null)
                {
                    final HashMap<String, String> myHashAlarm = new HashMap<>();
                    myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
                    myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, msg);

                    //System.out.println("send msg");
                    tts.speak(msg, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
                }
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

    private boolean IsLoaded()
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