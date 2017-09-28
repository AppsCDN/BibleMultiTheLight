
package org.hlwd.bible;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

class BibleAdapter extends RecyclerView.Adapter<BibleAdapter.ViewHolder>
{
    ArrayList<VerseBO> lstVerse = null;
    private String markFav;
    private String markReading;
    private VerseBO verse;
    private String ref;
    private int tagPosition;
    private SCommon _s = null;

    BibleAdapter()
    {
        this.lstVerse = null;
    }

    private void SetMark(final Context context)
    {
        this.markFav = PCommon.GetPref(context, IProject.APP_PREF_KEY.FAV_SYMBOL, context.getString(R.string.favSymbolFavDefault));
        this.markReading = context.getString(R.string.favSymbolReadingDefault);
    }

    BibleAdapter(final Context context, final String tbbName, final int bNumber, final int cNumber, final int vNumber)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.GetVerse(tbbName, bNumber, cNumber, vNumber);
    }

    BibleAdapter(final Context context, final String tbbName, final int bNumber, final int cNumber, final int vNumberFrom, final int vNumberTo)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.GetVerses(tbbName, bNumber, cNumber, vNumberFrom, vNumberTo);

        this.SaveCacheSearch(context);
    }

/*
    public BibleAdapter(final Context context, final String tbbName, final int planId, final int planDayNumber, final int bNumberStart, final int cNumberStart, final int vNumberStart,  final int bNumberEnd, final int cNumberEnd, final int vNumberEnd)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = null;
        final boolean copy = _s.CopyCacheSearchForOtherBible(tbbName, planId, planDayNumber, bNumberStart, cNumberStart, vNumberStart, bNumberEnd, cNumberEnd, vNumberEnd);

        this.SaveCacheSearch(context);
    }
*/
    BibleAdapter(final Context context, final String bbName, final int bNumber, final int cNumber, final String searchString)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.SearchBible(bbName, bNumber, cNumber, searchString);

        this.SaveCacheSearch(context);
    }

    BibleAdapter(final Context context, final String tbbName, final int bNumber, final int cNumber)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.GetChapter(tbbName, bNumber, cNumber);
    }

    BibleAdapter(final Context context, final String bbName, final int bNumber, final String searchString)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.SearchBible(bbName, bNumber, searchString);

        this.SaveCacheSearch(context);
    }

    BibleAdapter(final Context context, final String bbName, final String searchString)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.SearchBible(bbName, searchString);

        this.SaveCacheSearch(context);
    }

    BibleAdapter(final Context context, final int searchId)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.SearchBible(searchId);
    }

    /***
     * Get all notes
     * @param context
     * @param bbName
     * @param searchString  Give NULL to get all notes
     * @param orderBy       Order by
     * @param markType      Mark type (NULL to get all types)
     */
    @SuppressWarnings("JavaDoc")
    BibleAdapter(final Context context, final String bbName, final String searchString, final int orderBy, final String markType)
    {
        CheckLocalInstance(context);
        SetMark(context);

        this.lstVerse = _s.SearchNotes(bbName, searchString, orderBy, markType);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        //private LinearLayout card_recipient;
        private TextView tv_ref;
        private TextView tv_text;
        private TextView tv_mark;

        ViewHolder(View view)
        {
            super(view);

            tv_ref = (TextView)view.findViewById(R.id.tv_ref);
            tv_text = (TextView)view.findViewById(R.id.tv_text);
            tv_mark = (TextView) view.findViewById(R.id.tv_mark);

            final Typeface typeface = PCommon.GetTypeface(view.getContext());
            if (typeface != null)
            {
                tv_text.setTypeface(typeface);
            }
            final int fontSize = PCommon.GetFontSize(view.getContext());
            tv_ref.setTextSize(fontSize);
            tv_text.setTextSize(fontSize);
        }
    }

    @Override
    public BibleAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType)
    {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_recipient, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BibleAdapter.ViewHolder viewHolder, int position)
    {
        //Current verse
        verse = lstVerse.get(position);
        ref = PCommon.ConcaT( verse.bName, " ", verse.cNumber, ".", verse.vNumber );
        tagPosition = position;

        //Mark
        switch (verse.mark)
        {
            case 0:
                break;

            case 1:
                viewHolder.tv_mark.setPadding(10, 0, 5, 0);
                viewHolder.tv_mark.setText( markFav );
                break;

            case 2:
                viewHolder.tv_mark.setPadding(10, 0, 5, 0);
                viewHolder.tv_mark.setText( markReading );
                break;
        }

        //Set view
        //TEST: (check modulo)
/*
        if (position == 2 || position == 3 || position == 6 || position == 7) {
            viewHolder.card_recipient.setPadding(10, 0, 0, 0);
        }
*/

        viewHolder.tv_ref.setText(ref);
        viewHolder.tv_ref.setId(verse.id);
        viewHolder.tv_ref.setTag(tagPosition);

        viewHolder.tv_text.setText(verse.vText);
        viewHolder.tv_text.setId(verse.id);
        viewHolder.tv_text.setTag(tagPosition);

        //Events
        viewHolder.tv_ref.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                final TextView tvRef = (TextView) view;
                if (tvRef == null) return false;

                final int bibleId = tvRef.getId();
                final int position = Integer.parseInt( tvRef.getTag().toString() );
                PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.BIBLE_ID, bibleId);
                PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, position);

                return false;
            }
        });
        viewHolder.tv_text.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                final TextView tvText = (TextView) view;
                if (tvText == null) return false;

                final int bibleId = tvText.getId();
                final int position = Integer.parseInt( tvText.getTag().toString() );
                PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.BIBLE_ID, bibleId);
                PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, position);

                return false;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return lstVerse == null ? 0 : lstVerse.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        final VerseBO verse = lstVerse.get(position);

        return verse.mark;
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

    private void SaveCacheSearch(final Context context)
    {
        try
        {
            ArrayList<Integer> lstId = new ArrayList<>();

            if (lstVerse != null)
            {
                for (VerseBO verse : lstVerse) {
                    lstId.add(verse.id);
                }
            }

            _s.SaveCacheSearch(lstId);
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(context, ex);
        }
    }
}
