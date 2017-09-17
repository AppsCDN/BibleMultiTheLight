
package org.hlwd.bible;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogFragment extends Fragment
{
    private SCommon _s = null;
    private Context _context = null;
    private View v = null;
    private TextView tvLog;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        try
        {
            super.onCreateView(inflater, container, savedInstanceState);

            CheckLocalInstance();

            v = inflater.inflate(R.layout.fragment_log, container, false);
            setHasOptionsMenu(true);

            tvLog = (TextView) v.findViewById(R.id.tvLog);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return v;
    }

    @Override
    public void onResume()
    {
        try
        {
            super.onResume();

            CheckLocalInstance();

            final String logs = _s.GetAllLogs();
            tvLog.setText(PCommon.ConcaT(PCommon.GetPref(_context, IProject.APP_PREF_KEY.LOG_STATUS, ""), "\n\n", logs));
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater)
    {
        try
        {
            super.onCreateOptionsMenu(menu, menuInflater);

            menuInflater.inflate(R.menu.menu_log, menu);
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        final int id = item.getItemId();

        switch (id)
        {
            case R.id.mnu_log_clear:

                PCommon.ClearErrorLogs(_context);
                onResume();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Check local instance (to copy reader all activities that use it)
     */
    private void CheckLocalInstance()
    {
        if (_context == null) _context = getActivity().getApplicationContext();                     //Changed

        CheckLocalInstance(_context);
    }

    /***
    * Check local instance (to copy reader all activities that use it)
    */
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
}

