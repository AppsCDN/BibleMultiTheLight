
package org.hlwd.bible;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class BibleArticleAdapter extends RecyclerView.Adapter<BibleArticleAdapter.ViewHolder>
{
    private final ArrayList<SectionBO> lstSection = new ArrayList<>();
    private SCommon _s = null;
    private String _bbName = null;

    BibleArticleAdapter(final Context context, final String tbbName, final String content)
    {
        _bbName = tbbName.substring(0, 1);

        CheckLocalInstance(context);

        SectionBO section;
        int pos;
        int i = -1;
        String astrBQ;
        String[] arrBQ = content.split("<blockquote>");
        if (arrBQ.length > 0)
        {
            for (String strBQ : arrBQ)
            {
                if (strBQ == null)
                {
                    continue;
                }

                strBQ = strBQ.trim();
                if (strBQ.equalsIgnoreCase("<br>") || strBQ.equalsIgnoreCase("<br><br>"))
                {
                    continue;
                }

                i++;

                section = new SectionBO();
                section.id = i;

                pos = strBQ.indexOf("</blockquote>");
                if (pos >= 0)
                {
                    pos += 13;

                    if (strBQ.length() - 1 > pos)
                    {
                        astrBQ = strBQ.substring(pos);
                        if (astrBQ.equalsIgnoreCase("<br>") || astrBQ.equalsIgnoreCase("<br><br>"))
                        {
                            astrBQ = null;
                        }
                    }
                    else
                    {
                        astrBQ = null;
                    }
                    strBQ = strBQ.substring(0, pos - 13);

                    section.before = null;
                    section.content = strBQ;
                    section.after = astrBQ;
                }
                else
                {
                    section.before = strBQ;
                    section.content = null;
                    section.after = null;
                }

                this.lstSection.add(section);
            }
        }

        //noinspection UnusedAssignment
        arrBQ = null;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tv_before;
        private final TextView tv_text;
        private final TextView tv_after;

        ViewHolder(View view)
        {
            super(view);

            tv_before = (TextView)view.findViewById(R.id.tv_before);
            tv_text = (TextView)view.findViewById(R.id.tv_text);
            tv_after = (TextView)view.findViewById(R.id.tv_after);

            final Typeface typeface = PCommon.GetTypeface(view.getContext());
            if (typeface != null)
            {
                tv_before.setTypeface(typeface);
                tv_text.setTypeface(typeface);
                tv_after.setTypeface(typeface);
            }

            final int fontSize = PCommon.GetFontSize(view.getContext());
            tv_before.setTextSize(fontSize);
            tv_text.setTextSize(fontSize);
            tv_after.setTextSize(fontSize);
        }
    }

    @Override
    public BibleArticleAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType)
    {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_article_recipient, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BibleArticleAdapter.ViewHolder viewHolder, final int position)
    {
        SectionBO section = lstSection.get(position);
        Spanned spanned;

        if (section.before != null)
        {
            spanned = Html.fromHtml(section.before);
            viewHolder.tv_before.setVisibility(View.VISIBLE);
            viewHolder.tv_before.setText(spanned);
            viewHolder.tv_before.setId(section.id);
            viewHolder.tv_before.setTag(position);
            if (section.before.contains("</a>")) viewHolder.tv_before.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (section.content != null)
        {
            //test:
            //SpannableStringBuilder ssb = new SpannableStringBuilder();
            //ssb.append(spanned);

            spanned = Html.fromHtml(section.content);
            viewHolder.tv_text.setVisibility(View.VISIBLE);
            viewHolder.tv_text.setText(spanned);
            viewHolder.tv_text.setId(section.id);
            viewHolder.tv_text.setTag(position);
            viewHolder.tv_text.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    try
                    {
                        final TextView tvText = (TextView) view;
                        if (tvText == null) return false;

                        final String content = tvText.getText().toString();
                        final int endRef = content.indexOf(":");
                        final String completeRef = content.substring(0, endRef).replace(".", " ");
                        final String[] ref = completeRef.split("\\s");
                        final int bNumber = _s.GetBookNumberByName(ref[0]);
                        final int cNumber = Integer.parseInt(ref[1]);
                        final int vNumber = Integer.parseInt(ref[2]);
                        final ArrayList<VerseBO> lstVerse = _s.GetVerse(_bbName, bNumber, cNumber, vNumber);

                        final int bibleId = (lstVerse != null && lstVerse.size() > 0) ? lstVerse.get(0).id : 0;
                        final int position = Integer.parseInt( tvText.getTag().toString() );
                        PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.BIBLE_ID, bibleId);
                        PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, position);
                    }
                    catch (Exception ignored) { }

                    return false;
                }
            });
            if (section.content.contains("</a>")) viewHolder.tv_text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (section.after != null)
        {
            spanned = Html.fromHtml(section.after);
            viewHolder.tv_after.setVisibility(View.VISIBLE);
            viewHolder.tv_after.setText(spanned);
            viewHolder.tv_after.setId(section.id);
            viewHolder.tv_after.setTag(position);
            if (section.after.contains("</a>")) viewHolder.tv_after.setMovementMethod(LinkMovementMethod.getInstance());
        }

        /*
        final Typeface typeface = PCommon.GetTypeface(_context);
        if (typeface != null)
        {
            viewHolder.tv_text.setTypeface(typeface);
        }
        final int verseSize = PCommon.GetFontSize(_context);
        viewHolder.tv_text.setTextSize(verseSize);
        */
    }

    @Override
    public int getItemCount()
    {
        return lstSection == null ? 0 : lstSection.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
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
}
