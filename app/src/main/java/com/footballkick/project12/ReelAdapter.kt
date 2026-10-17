package com.footballkick.project12

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.getSystem
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ReelAdapter(
    private val context: Context,
    private val index: Int,
    private val reelsSymbols: List<Int>

) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {
    private var itemCountToShow = 5
    private var slotHeight = 410f
    private var pos: Int = 0
    private var line3: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reel_item, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val dp:Float=slotHeight/itemCountToShow
        holder.itemView.layoutParams.height = convertDpToPixel(dp,context).toInt()
        pos = position
        line3 = reelsSymbols[position % reelsSymbols.size]

        holder.image.setImageResource(line3)

        var x = 0
        if (Data.spinNumber != 0)
            x = 60 * Data.spinNumber
        else x = 60

        when (index) {
            0 -> {
                if ((pos % x) == (x - 2)) {
                    Data.position[0] = pos
                }
                if (pos==x) {
                    Data.line1[0] = reelsSymbols[position - 4]
                    Data.line2[0] = reelsSymbols[position - 3]
                    Data.line3[0] = reelsSymbols[position - 2]
                    Data.line4[0] = reelsSymbols[position - 1]
                    Data.line5[0] = reelsSymbols[position]
                }
            }

            1 -> {
                if (((pos % (x * 1.5)).toInt()) == ((x * 1.5).toInt() - 2)) {
                    Data.position[1] = pos
                }
                if (pos==(x*1.5).toInt()) {
                    Data.line1[1] = reelsSymbols[position - 4]
                    Data.line2[1] = reelsSymbols[position - 3]
                    Data.line3[1] = reelsSymbols[position - 2]
                    Data.line4[1] = reelsSymbols[position - 1]
                    Data.line5[1] = reelsSymbols[position]
                    }
            }

            2 -> {
                if ((pos % (x * 2)) == ((x * 2) - 2)) {
                    Data.position[2] = pos
                }
                if (pos==(x*2)) {
                    Data.line1[2] = reelsSymbols[position - 4]
                    Data.line2[2] = reelsSymbols[position - 3]
                    Data.line3[2] = reelsSymbols[position - 2]
                    Data.line4[2] = reelsSymbols[position - 1]
                    Data.line5[2] = reelsSymbols[position]
                }
            }

            3 -> {
                if (((pos % (x * 2.5)).toInt()) == ((x * 2.5).toInt() - 2)) {
                    Data.position[3] = pos
                }
                if (pos==(x*2.5).toInt()) {
                    Data.line1[3] = reelsSymbols[position - 4]
                    Data.line2[3] = reelsSymbols[position - 3]
                    Data.line3[3] = reelsSymbols[position - 2]
                    Data.line4[3] = reelsSymbols[position - 1]
                    Data.line5[3] = reelsSymbols[position]
                }
            }

            4 -> {
                if ((pos % (x * 3)) == ((x * 3) - 2)) {
                    Data.position[4] = pos
                }
                if (pos==(x*3)) {
                    Data.line1[4] = reelsSymbols[position - 4]
                    Data.line2[4] = reelsSymbols[position - 3]
                    Data.line3[4] = reelsSymbols[position - 2]
                    Data.line4[4] = reelsSymbols[position - 1]
                    Data.line5[4] = reelsSymbols[position]
                }
            }
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE
    class ReelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}