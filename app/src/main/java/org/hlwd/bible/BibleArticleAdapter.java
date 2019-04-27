
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
    private SCommon _s = null;
    private final ArrayList<SectionBO> lstSection = new ArrayList<>();
    private ArrayList<ShortSectionBO> lstShortSection = new ArrayList<>();
    @SuppressWarnings("UnusedAssignment")
    private String bbName = null;
    private final boolean isUiTelevision = true;
    @SuppressWarnings("UnusedAssignment")
    private Context _context = null;
    private int id = -1;
    private int blockId = -1;
    private enum BLOCK_TYPE { BEFORE, CONTENT }

    BibleArticleAdapter(final Context context, final String tbbName, final ArtOriginalContentBO artOriginalContent)
    {
        try
        {
            _context = context;

            CheckLocalInstance(context);

            bbName = tbbName.substring(0, 1);
            //was: isUiTelevision = PCommon.IsUiTelevision(context);

            BuildListSectionForTv(artOriginalContent);

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
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
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

                this.lstSection.add(section);
            }
        }

        //noinspection UnusedAssignment
        arrBQ = null;
    }
    */

    /***
     * Get article
     * @param artOriginalContent    Original content
     */
    @SuppressWarnings("JavaDoc")
    private String GetArticle(final ArtOriginalContentBO artOriginalContent)
    {
        if (artOriginalContent == null) return "";

        String artHtml = artOriginalContent.originalContent;

        try
        {
            final String ha = artOriginalContent.title;

            int i = -1;
            int pos;

            //Parse <R></R>
            String[] arrRef = artHtml.split("<R>");
            if (arrRef.length > 0)
            {
                String strVerses;
                for (String strRef : arrRef)
                {
                    i++;

                    if (strRef != null)
                    {
                        pos = strRef.indexOf("</R>");
                        if (pos >= 0)
                        {
                            pos = pos + 4;
                            strRef = strRef.substring(0, pos);
                            strRef = PCommon.ConcaT("<R>", strRef);
                            arrRef[ i ] = strRef;
                            String[] ref = strRef.replaceFirst("<R>", "").replaceFirst("</R>", "").split("\\s");
                            if (ref.length == 4) {
                                strVerses = _s.GetVersesHtml(bbName, Integer.parseInt(ref[0]), Integer.parseInt(ref[1]), Integer.parseInt(ref[2]), Integer.parseInt(ref[3]));
                            }
                            else
                            {   //5
                                strVerses = _s.GetVersesHtml(ref[0], Integer.parseInt(ref[1]), Integer.parseInt(ref[2]), Integer.parseInt(ref[3]),  Integer.parseInt(ref[4]));
                            }
                            //noinspection UnusedAssignment
                            ref = null;
                            artHtml = artHtml.replaceFirst(strRef, strVerses);
                        }
                    }
                }
                //noinspection UnusedAssignment
                strVerses = null;
            }

            //Parse <HB></HB>
            i = -1;
            //noinspection UnusedAssignment
            arrRef = null;
            arrRef = artHtml.split("<HB>");
            if (arrRef.length > 0)
            {
                for (String strRef : arrRef)
                {
                    i++;

                    if (strRef != null)
                    {
                        pos = strRef.indexOf("</HB>");
                        if (pos >= 0)
                        {
                            pos = pos + 5;
                            strRef = strRef.substring(0, pos);
                            strRef = PCommon.ConcaT("<HB>", strRef);
                            arrRef[ i ] = strRef;
                            String[] ref = strRef.replaceFirst("<HB>", "").replaceFirst("</HB>", "").split("\\s");
                            artHtml = artHtml.replaceFirst(strRef,
                                    PCommon.ConcaT("<HS>", _s.GetBookRef( bbName, Integer.parseInt(ref[0]) ).bName, "</HS>"));
                            //noinspection UnusedAssignment
                            ref = null;
                        }
                    }
                }
            }
            //noinspection UnusedAssignment
            arrRef = null;

            //Parse <T></T>,  <H></H>,  <HA/>
            if (ha != null)
            {
                artHtml = artHtml.replaceFirst("<HA/>", ha);
            }

            artHtml = artHtml
                .replaceAll("<HS>", "<br><span><u>")
                .replaceAll("</HS>", "</u></span>")
                .replaceAll("<H>", "<h1><u>")
                .replaceAll("</H>", "</u></h1>");
            //End Parse
            //TODO FAB: see spannableString(builder) for titles without too much space after.
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }

        return artHtml;
    }

    private void BuildListSectionForTv(final ArtOriginalContentBO artOriginalContent)
    {
        try
        {
            final String finalContent = GetArticle(artOriginalContent);

            final String mainDelimiterStart = "<blockquote>";
            final String mainDelimiterEnd = mainDelimiterStart.replaceFirst("<", "</");
            final int mainDelimiterEndSize = mainDelimiterEnd.length();

            String[] arrBQ = finalContent.split(mainDelimiterStart);
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

            //let's prepare the generated code
            int from_id;
            int bId;
            String contentBefore;
            String content;
            String contentAfter;
            String refStart;
            int refCount;
            int refVerseCount;
            int refFrom;
            int sectionIndex = 0;
            int sectionLastIndex = (lstSection.size() - 1) < 0 ? 0 : lstSection.size() - 1;
            SectionBO section;

            while (sectionIndex <= sectionLastIndex)
            {
                section = lstSection.get(sectionIndex);

                from_id = section.id;
                bId = section.blockId;

                //before
                contentBefore = "";
                if (section.before != null)
                {
                    contentBefore = section.before.endsWith("<br>") ? section.before : PCommon.ConcaT(section.before, "<br>");
                }

                //after
                contentAfter = "";
                if (section.after != null)
                {
                    contentAfter = section.after.endsWith("<br>") ? section.after : PCommon.ConcaT(section.after, "<br>");
                }

                //content
                refCount = 0;
                refVerseCount = 0;
                refStart = "";
                refFrom = 1;
                while (section.blockRef != null && section.blockId == bId)
                {
                    //it's REF
                    if (section.blockSubId == 0)
                    {
                        //it's start ref
                        final String[] words = section.blockRef.split("\\s");
                        refFrom = Integer.parseInt(words[ 2 ]);
                        refStart = PCommon.ConcaT(_s.GetBookNumberByName(bbName, words[ 0 ]), " ", words[ 1 ], " ", words[ 2 ]);
                        refCount = 1;
                        refVerseCount = 1;
                    }
                    else
                    {
                        //count sections
                        refCount++;

                        //count verses
                        if (section.content.startsWith("<b>")) refVerseCount++;
                    }

                    if ((sectionIndex + refCount) > sectionLastIndex) break;

                    //test next section
                    section = lstSection.get(sectionIndex + refCount);
                }
                final int refTo = refFrom - 1 + refVerseCount;
                content = refStart.equalsIgnoreCase("") ? "" : PCommon.ConcaT("<R>", refStart, " ", refTo, "</R>");

                //final
                content = PCommon.ConcaT(contentBefore, content, contentAfter);
                if (!content.equalsIgnoreCase(""))
                {
                    lstShortSection.add(new ShortSectionBO(bId, content, from_id));
                }

                //skip sections
                sectionIndex += (refCount > 1) ? refCount - 1 : 1;
            }

            //concatenate same blocks
            final int lastBlockId = lstSection.get(sectionLastIndex).blockId;
            final ArrayList<ShortSectionBO> lstShortSectionSimplified = new ArrayList<>();
            String sumContent;
            int sumFrom_id;
            for (int blockId = 0; blockId <= lastBlockId; blockId++)
            {
                sumContent = "";
                sumFrom_id = -1;
                for (final ShortSectionBO shortSection : lstShortSection)
                {
                    if (shortSection.blockId == blockId)
                    {
                        content = shortSection.content;
                        if (content != null)
                        {
                            sumContent = PCommon.ConcaT(sumContent, content);
                        }

                        if (sumFrom_id == -1)
                        {
                            sumFrom_id = shortSection.from_id;
                        }
                    }
                }
                lstShortSectionSimplified.add(new ShortSectionBO(blockId, sumContent, sumFrom_id));
            }
            lstShortSection = lstShortSectionSimplified;

            //noinspection UnusedAssignment
            arrBQ = null;
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    private void SplitStringForTv(final String strToSplit, final BLOCK_TYPE blockType)
    {
        try
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

                this.lstSection.add(section);
            }

            //noinspection UnusedAssignment
            arrStr = null;
        }
        catch(Exception ex)
        {
            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
        }
    }

    ArrayList<ShortSectionBO> GetArticleShortSections()
    {
        return this.lstShortSection;
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

            tv_before = view.findViewById(R.id.tv_before);
            tv_text = view.findViewById(R.id.tv_text);
            tv_after = view.findViewById(R.id.tv_after);

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
                tv_before0 = view.findViewById(R.id.tv_before0);
                tv_text_space_before = view.findViewById(R.id.tv_text_space_before);
                tv_text0 = view.findViewById(R.id.tv_text0);

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
            final SectionBO section = lstSection.get(position);
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
                else
                {
                    vwh_tv_before.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view)
                        {
                            PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.BIBLE_ID, -1);
                            PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, position);

                            return false;
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
                        catch (Exception ex)
                        {
                            if (PCommon._isDebugVersion) PCommon.LogR(_context, ex);
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
                    else
                    {
                        vwh_tv_after.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view)
                            {
                                PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.BIBLE_ID, -1);
                                PCommon.SavePrefInt(view.getContext(), IProject.APP_PREF_KEY.VIEW_POSITION, position);

                                return false;
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
        return lstSection.size();
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
