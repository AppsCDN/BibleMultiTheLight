
package org.hlwd.bible;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class BibleArticleAdapter extends RecyclerView.Adapter<BibleArticleAdapter.ViewHolder>
{
    private final ArrayList<SectionBO> _lstSection = new ArrayList<>();
    private SCommon _s = null;
    private String bbName = null;
    private final boolean isUiTelevision = true;
    private Context _context = null;
    private int id = -1;
    private int blockId = -1;
    private enum BLOCK_TYPE { BEFORE, CONTENT }

    BibleArticleAdapter(final Context context, final String tbbName, final String content)
    {
        _context = context;

        CheckLocalInstance(context);

        bbName = tbbName.substring(0, 1);
        //was: isUiTelevision = PCommon.IsUiTelevision(context);

        BuildListSectionForTv(content);

        /*was :
        if (isUiTelevision)
        {
            BuildListSectionForTv(content);
        }
        else
        {
            BuildListSectionForOther(content);
        }
        */
    }

    /*
    private void BuildListSectionForOther(final String content)
    {
        final String mainDelimiterStart = "<blockquote>";
        final String mainDelimiterEnd = mainDelimiterStart.replaceFirst("<", "</");
        final int mainDelimiterEndSize = mainDelimiterEnd.length();

        String[] arrBQ = content.split(mainDelimiterStart);
        if (arrBQ.length > 0)
        {
            SectionBO section;
            int pos;
            String astrBQ;

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

                section = new SectionBO();
                section.id = ++id;

                pos = strBQ.indexOf(mainDelimiterEnd);
                if (pos >= 0)
                {
                    pos += mainDelimiterEndSize;

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
                    strBQ = strBQ.substring(0, pos - mainDelimiterEndSize);

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

                this._lstSection.add(section);
            }
        }

        //noinspection UnusedAssignment
        arrBQ = null;
    }
    */

    private void BuildListSectionForTv(final String content)
    {
        final String mainDelimiterStart = "<blockquote>";
        final String mainDelimiterEnd = mainDelimiterStart.replaceFirst("<", "</");
        final int mainDelimiterEndSize = mainDelimiterEnd.length();

        String[] arrBQ = content.split(mainDelimiterStart);
        if (arrBQ.length > 0)
        {
            int pos;
            String astrBQ;

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

                pos = strBQ.indexOf(mainDelimiterEnd);
                if (pos >= 0)
                {
                    pos += mainDelimiterEndSize;

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
                    strBQ = strBQ.substring(0, pos - mainDelimiterEndSize);

                    //Content = strBQ
                    SplitStringForTv(strBQ, BLOCK_TYPE.CONTENT);

                    //After = astrBQ
                    if (astrBQ == null) continue;
                    SplitStringForTv(astrBQ, BLOCK_TYPE.BEFORE);
                }
                else
                {
                    //Before = strBQ
                    SplitStringForTv(strBQ, BLOCK_TYPE.BEFORE);
                }
            }
        }

        //noinspection UnusedAssignment
        arrBQ = null;
    }

    private void SplitStringForTv(final String strToSplit, final BLOCK_TYPE blockType)
    {
        final String sentenceDelimiter = "<br> |<br>|(?<=\\. )";
        SectionBO section;
        int blockSubId = -1;
        final String blockRef = (blockType == BLOCK_TYPE.CONTENT) ? strToSplit.substring(0, strToSplit.indexOf(":")).replace(".", " ").replace("<b>", "") : null;

        blockId++;
        String[] arrStr = strToSplit.split(sentenceDelimiter);
        for (String str : arrStr)
        {
            if (str == null)
            {
                continue;
            }

            section = new SectionBO();
            section.id = ++id;
            section.blockId = blockId;
            section.blockSubId = ++blockSubId;
            section.blockRef = blockRef;

            if (blockType == BLOCK_TYPE.BEFORE)
            {
                section.before = str;
                section.content = null;
                section.after = null;
            }
            else
            {
                section.before = null;
                section.content = str;
                section.after = null;
            }

            this._lstSection.add(section);
        }

        //noinspection UnusedAssignment
        arrStr = null;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView tv_before;
        private final TextView tv_before0;
        private final TextView tv_text;
        private final TextView tv_text0;
        private final TextView tv_after;
        private final TextView tv_text_space_before;

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
                if (tv_after != null) tv_after.setTypeface(typeface);
            }

            final int fontSize = PCommon.GetFontSize(view.getContext());
            tv_before.setTextSize(fontSize);
            tv_text.setTextSize(fontSize);
            if (tv_after != null) tv_after.setTextSize(fontSize);

            //noinspection ConstantConditions
            if (isUiTelevision)
            {
                tv_before0 = (TextView)view.findViewById(R.id.tv_before0);
                tv_text_space_before = (TextView)view.findViewById(R.id.tv_text_space_before);
                tv_text0 = (TextView)view.findViewById(R.id.tv_text0);

                tv_before0.setTypeface(typeface);
                tv_text_space_before.setTypeface(typeface);
                tv_text0.setTypeface(typeface);

                tv_before0.setTextSize(fontSize);
                tv_text_space_before.setTextSize(fontSize);
                tv_text0.setTextSize(fontSize);
            }
            else
            {
                tv_before0 = null;
                tv_text_space_before = null;
                tv_text0 = null;
            }
        }
    }

    @Override
    public BibleArticleAdapter.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType)
    {
        @SuppressWarnings("ConstantConditions") final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(isUiTelevision ? R.layout.card_article_recipient_tv : R.layout.card_article_recipient, viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BibleArticleAdapter.ViewHolder viewHolder, final int position)
    {
        try
        {
            final SectionBO section = _lstSection.get(position);
            if (section.before != null)
            {
                @SuppressWarnings("ConstantConditions") final TextView vwh_tv_before = (isUiTelevision && section.blockSubId == 0) ? viewHolder.tv_before0 : viewHolder.tv_before;
                final Spanned spanned = Html.fromHtml(section.before);
                vwh_tv_before.setVisibility(View.VISIBLE);
                vwh_tv_before.setText(spanned);
                vwh_tv_before.setId(section.id);
                vwh_tv_before.setTag(position);
                if (section.before.trim().equalsIgnoreCase("")) vwh_tv_before.setFocusable(false);
                if (section.before.contains("</a>"))
                {
                   vwh_tv_before.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           PCommon.OpenUrl(v.getContext(), vwh_tv_before.getUrls()[0].getURL());
                       }
                   });
                }
            }

            if (section.content != null)
            {
                //test:
                //SpannableStringBuilder ssb = new SpannableStringBuilder();
                //ssb.append(spanned);
                //noinspection ConstantConditions
                if (isUiTelevision && section.blockSubId == 0)
                {
                    final TextView vwh_tv_space_text_before = viewHolder.tv_text_space_before;
                    vwh_tv_space_text_before.setVisibility(View.VISIBLE);
                }
                @SuppressWarnings("ConstantConditions") final TextView vwh_tv_text = (isUiTelevision && section.blockSubId == 0) ? viewHolder.tv_text0 : viewHolder.tv_text;
                final Spanned spanned = Html.fromHtml(section.content);
                vwh_tv_text.setVisibility(View.VISIBLE);
                vwh_tv_text.setText(spanned);
                //noinspection ConstantConditions
                if (isUiTelevision) vwh_tv_text.setTag(R.id.tv1, section.blockRef);
                vwh_tv_text.setTag(position);
                vwh_tv_text.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        try
                        {
                            final TextView tvText = (TextView) view;
                            if (tvText == null) return false;
                            @SuppressWarnings("unused") final String content = tvText.getText().toString();
                            @SuppressWarnings("ConstantConditions") final String completeRef = (!isUiTelevision) ? content.substring(0, content.indexOf(":")).replace(".", " ") : (String)view.getTag(R.id.tv1);
                            if (completeRef == null) return false;
                            final String[] ref = completeRef.split("\\s");
                            final int bNumber = _s.GetBookNumberByName(ref[0]);
                            final int cNumber = Integer.parseInt(ref[1]);
                            final int vNumber = Integer.parseInt(ref[2]);
                            final ArrayList<VerseBO> lstVerse = _s.GetVerse(bbName, bNumber, cNumber, vNumber);
                            final int bibleId = (lstVerse != null && lstVerse.size() > 0) ? lstVerse.get(0).id : 0;
                            final int position = Integer.parseInt( tvText.getTag().toString() );
                            PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.BIBLE_ID, bibleId);
                            PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, position);
                        }
                        catch (Exception ignored)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(_context, ignored);
                        }

                        return false;
                    }
                });
                if (section.content.trim().equalsIgnoreCase("")) vwh_tv_text.setFocusable(false);
                if (section.content.contains("</a>"))
                {
                    vwh_tv_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PCommon.OpenUrl(v.getContext(), vwh_tv_text.getUrls()[0].getURL());
                        }
                    });
                }
            }

            //noinspection ConstantConditions
            if (!isUiTelevision)
            {
                if (section.after != null)
                {
                    final TextView vwh_tv_after = viewHolder.tv_after;
                    final Spanned spanned = Html.fromHtml(section.after);
                    vwh_tv_after.setVisibility(View.VISIBLE);
                    vwh_tv_after.setText(spanned);
                    vwh_tv_after.setId(section.id);
                    vwh_tv_after.setTag(position);
                    if (section.after.contains("</a>"))
                    {
                        vwh_tv_after.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PCommon.OpenUrl(v.getContext(), vwh_tv_after.getUrls()[0].getURL());
                            }
                        });
                    }
                }
            }
        }
        catch (Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
        return _lstSection == null ? 0 : _lstSection.size();
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
