package com.footballkick.project12

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.footballkick.project12.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var recyclerViews: List<RecyclerView>
    private var isBonus: Boolean = false
    private var isBonusPopUpShown: Boolean = false
    private var totalWin: Int = 0
    private val win = 50
    private var bet = 10
    private var savedBet = 0
    private var balance = 1000
    private var bonusNumber = 0
    private var bonusSpinNumber = 0
    private lateinit var fadeIn: Animation
    private lateinit var fadeOut: Animation
    private var x: Array<Float> = arrayOf(0f, 0f, 0f, 0f, 0f)
    private var y: Array<Float?> = arrayOf(0f, 0f, 0f, 0f, 0f)
    private var bonusX: Array<Float> = arrayOf(
        0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    private var bonusY: Array<Float?> = arrayOf(
        0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        recyclerViews = listOf(
            binding.reel1,
            binding.reel2,
            binding.reel3,
            binding.reel4,
            binding.reel5
        )

        val symbols = listOf(
            R.drawable.i1,
            R.drawable.i2,
            R.drawable.i3,
            R.drawable.i4,
            R.drawable.i5,
            R.drawable.i6,
            R.drawable.i7
        )

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        val listSize = 100000
        val listOfReels = List(5) {
            MutableList(listSize) { symbols.random() }
        }

        val bonusPeriodicity = 30
        val rand = List(5) { mutableSetOf<Int>() }
        for (m in 0..4) {
            val bonusRepeatPerColum = listSize / bonusPeriodicity
            while (rand[m].size <= bonusRepeatPerColum) {
                val randomNumber = (0 until listSize).random()
                rand[m].add(randomNumber)
                listOfReels[m][randomNumber] = R.drawable.bonus
            }
        }

        recyclerViews.forEachIndexed { index, recyclerView ->
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)

        }

        recyclerViews[0].adapter = ReelAdapter(this, 0, listOfReels[0])
        recyclerViews[1].adapter = ReelAdapter(this, 1, listOfReels[1])
        recyclerViews[2].adapter = ReelAdapter(this, 2, listOfReels[2])
        recyclerViews[3].adapter = ReelAdapter(this, 3, listOfReels[3])
        recyclerViews[4].adapter = ReelAdapter(this, 4, listOfReels[4])

        Handler().postDelayed({
            recyclerViews.forEach {
                it.scrollToPosition(0)
                it.isNestedScrollingEnabled = false
            }
        }, 30)

        Handler().postDelayed({
            val layoutManager = recyclerViews[4].layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                val view = layoutManager.findViewByPosition(i)
                x[i] = recyclerViews[i].x.plus(recyclerViews[i].width / 2)
                y[i] = view?.y?.plus(view.height / 2)
            }
        }, 40)

        binding.plus.setOnClickListener {
            if (bet < 100) {
                bet += 10
                binding.bet.text = "$bet"
            }
        }

        binding.minus.setOnClickListener {
            if (bet > 10) {
                bet -= 10
                binding.bet.text = "$bet"
            }
        }

        var reelPos = 0

        binding.start.setOnClickListener {

            if (isBonus && bonusSpinNumber > 0) {
                bonusSpinNumber -= 1
                binding.bet.text = "$bonusSpinNumber FREE"
            }

            if (Data.spinNumber == 500) {
                recyclerViews.forEach {
                    it.scrollToPosition(0)
                    Data.spinNumber = 0
                    reelPos = 0
                }
                Handler().postDelayed({
                    recyclerViews[0].smoothScrollToPosition(reelPos)
                    recyclerViews[1].smoothScrollToPosition((reelPos * 1.5).toInt())
                    recyclerViews[2].smoothScrollToPosition(reelPos * 2)
                    recyclerViews[3].smoothScrollToPosition((reelPos * 2.5).toInt())
                    recyclerViews[4].smoothScrollToPosition(reelPos * 3)
                }, 1)
            }
            Data.spinNumber += 1

            if (!isBonus) {
                balance -= bet
            }

            binding.balance.text = "$balance"
            binding.winName.text = ""
            binding.win.text = ""
            binding.coinWin.visibility = View.GONE

            if (binding.balance.text.toString().toInt() <= 0) {
                balance += 250
                binding.winName.text = "LUCKY YOU"
                binding.win.text = "+250!"
                binding.balance.text = "$balance"
            }

            reelPos += 60

            binding.start.isEnabled = false
            binding.plus.isEnabled = false
            binding.minus.isEnabled = false

            Handler().postDelayed({
                Handler().postDelayed({
                    binding.start.isEnabled = true
                    if (!isBonus) {
                        binding.plus.isEnabled = true
                        binding.minus.isEnabled = true
                    }

                }, 190)

                findConsecutiveIdenticalIntegers()

                if (isBonus && bonusSpinNumber == 0) {
                    deActivateBonus()
                }

                if (isBonus) {
                    binding.bet.text = "$bonusSpinNumber FREE"
                    if (!isBonusPopUpShown) {
                        popUpText()
                        isBonusPopUpShown = true
                    }
                }
            }, 2510)

            recyclerViews[0].smoothScrollToPosition(reelPos)
            recyclerViews[1].smoothScrollToPosition((reelPos * 1.5).toInt())
            recyclerViews[2].smoothScrollToPosition(reelPos * 2)
            recyclerViews[3].smoothScrollToPosition((reelPos * 2.5).toInt())
            recyclerViews[4].smoothScrollToPosition(reelPos * 3)
        }
    }

    private fun findConsecutiveIdenticalIntegers() {
        bonusNumber = 0
        var result: MutableList<Array<Int>> = if (!isBonus) {
            mutableListOf(Data.line2, Data.line3, Data.line4)
        } else {
            mutableListOf(Data.line1, Data.line2, Data.line3, Data.line4, Data.line5)
        }
        var y1: Array<Float?> = if (!isBonus) {
            arrayOf(y[1], y[2], y[3])
        } else {
            arrayOf(y[0], y[1], y[2], y[3], y[4])
        }

        var currentInteger: Int? = null
        var consecutiveCount = 1
        var consecutivePosition = 0
        var consecutiveID = -1

        for (n in 0 until result.size) {
            val yPoint = y1[n]
            consecutiveCount = 1

            if (result[n][0] == R.drawable.bonus) {
                bonusX[bonusNumber] = x[0]
                bonusY[bonusNumber] = yPoint
                bonusNumber += 1
                Log.d("Log", "bonusNumber = $bonusNumber")
            }
            for (i in 0 until result[n].size - 1) {
                if (result[n][i + 1] == R.drawable.bonus) {
                    bonusX[bonusNumber] = x[i + 1]
                    bonusY[bonusNumber] = yPoint
                    bonusNumber += 1
                    Log.d("Log", "bonusNumber = $bonusNumber")
                }
                if (result[n][i] == result[n][i + 1]) {
                    consecutiveCount++
                    if (consecutiveCount >= 3 && currentInteger != result[n][i]) {
                        consecutivePosition = i - consecutiveCount + 2
                        currentInteger = result[n][i]
                        consecutiveID = result[n][i]
                    }
                    if (consecutivePosition != -1 && consecutiveCount >= 3) {
                        totalWin += (consecutiveCount - 2) * (win * (bet / 10))
                        Log.d("Log", "totalWin = $totalWin")
                        for (index2 in consecutivePosition until consecutivePosition + consecutiveCount) {
                            createImage(x[index2], yPoint!!, consecutiveID)
                        }
                    }
                } else {
                    consecutiveCount = 1
                    currentInteger = null
                }
            }
        }

        if (bonusNumber >= 3) {
            isBonusPopUpShown = false

            Log.d("Log", "DRAWING BONUSES, bonusNumber = $bonusNumber")
            for (index3 in 0 until bonusNumber) {
                createImage(bonusX[index3], bonusY[index3]!!, R.drawable.bonus)
            }
            if (!isBonus) {
                binding.pupUpTxt.text = "+${5 + ((bonusNumber - 3) * 3)} FREE Spins"
                bonusSpinNumber = 5 + ((bonusNumber - 3) * 3)
            } else {
                binding.pupUpTxt.text = "+${bonusNumber - 1} FREE Spins"
                bonusSpinNumber += bonusNumber - 1
            }
            isBonus = true
            activateBonus()
        }

        if (totalWin > 0) {
            Log.d("Log", "totalWin = $totalWin")
            balance += totalWin
            binding.win.text = "$totalWin"
            if (totalWin in 1..win * (bet / 10)) {
                binding.winName.text = "Win"
            }
            if (totalWin in (win * (bet / 10)) + 1..(win * (bet / 10)) * 2) {
                binding.winName.text = "BIG WIN"
            }
            if (totalWin >= ((win * (bet / 10)) * 2) + 1) {
                binding.winName.text = "SUPERWIN"
            }
            binding.balance.text = "$balance"

            binding.coinWin.visibility = View.VISIBLE

            totalWin = 0
        }
    }

    private fun createImage(x: Float, y: Float, image: Int) {
        val imageView = ImageView(this)
        val width = convertDpToPixel(80f, this).toInt()
        val height = convertDpToPixel(80f, this).toInt()
        imageView.setImageResource(image)

        val layoutParams = FrameLayout.LayoutParams(width, height)
        layoutParams.leftMargin = (x - width / 2).toInt()
        layoutParams.topMargin = (y - height / 2).toInt()
        binding.frameLayout.addView(imageView, layoutParams)
        imageView.animate().scaleX(1.2f).scaleY(1.2f).withEndAction {

            if (image == R.drawable.bonus) {
                Handler().postDelayed({
                    imageView.animate().scaleX(1f).scaleY(1f).withEndAction {
                        binding.frameLayout.removeView(imageView)
                    }.duration = 200
                }, 1250)
            } else {
                imageView.animate().scaleX(1f).scaleY(1f).withEndAction {
                    binding.frameLayout.removeView(imageView)
                }.duration = 250
            }
        }.duration = 250
    }

    private fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun activateBonus() {
        binding.botPanel.animate().scaleY(0f).duration = 1000
        binding.topPanel.animate().scaleY(0f).duration = 1000
        binding.plus.isEnabled = false
        binding.minus.isEnabled = false
        savedBet = bet
    }

    private fun deActivateBonus() {
        binding.botPanel.animate().scaleY(1f).duration = 500
        binding.topPanel.animate().scaleY(1f).duration = 500
        binding.plus.isEnabled = true
        binding.minus.isEnabled = true
        isBonus = false
        bet = savedBet
        binding.bet.text = "$bet"
    }

    private fun popUpText() {
        binding.popUp.visibility = View.VISIBLE
        binding.popUp.startAnimation(fadeIn)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.popUp.startAnimation(fadeOut)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.popUp.visibility = View.INVISIBLE
            }, 500)
        }, 1500)
    }
}
