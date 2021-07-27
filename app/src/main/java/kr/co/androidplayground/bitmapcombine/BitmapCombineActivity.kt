package kr.co.androidplayground.bitmapcombine

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kr.co.androidplayground.R
import kr.co.androidplayground.databinding.ActivityBitmapCombineBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BitmapCombineActivity : AppCompatActivity() {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityBitmapCombineBinding>(this, R.layout.activity_bitmap_combine)
    }

    // 테두리 반경
    private val cornerRadius = 10f.scale()

    // 이미지 크기
    private val imageSize = 217f.scale()

    // 카드 이미지 경로
    private lateinit var cardFilePath: String

    // 배경 이미지 경로
    private lateinit var bgFilePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnSave.setOnClickListener {
            sendInstagram()
        }

        try {
            createImageFile()
            start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendInstagram() {
        val backgroundAssetUri = Uri.parse(bgFilePath)
        val sourceApplication = "kr.co.androidplayground"
        Log.d("INSTAGRAM", backgroundAssetUri.toString())

        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            Log.d("INSTAGRAM", "Intent")
            setDataAndType(backgroundAssetUri, "image/png")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        startActivity(intent)
    }

    private fun createImageFile() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        storageDir?.let {
            // 카드 이미지 파일 생성
            File.createTempFile(
                "card_${timeStamp}_",
                ".png",
                it
            ).apply {
                cardFilePath = absolutePath
            }

            // 배경 이미지 파일 생성
            File.createTempFile(
                "bg_${timeStamp}_",
                ".png",
                it
            ).apply {
                bgFilePath = absolutePath
            }
        }
    }



    private fun start() {

        Glide.with(this@BitmapCombineActivity)
            .asBitmap()
            .override(imageSize.toInt(), imageSize.toInt())
            .apply(RequestOptions().transform(RoundedCorners(cornerRadius.toInt())))
            .load("https://images.pet-friends.co.kr/storage/pet_friends/challenge/title/title_9b2532bc-2700-488b-b5c3-ade2b4745593.jpg")
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.imageView.setImageBitmap(makeCardBitmap(resource))
                    binding.imageBackground.setImageBitmap(makeBackgroundBitmap(resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    /**
     * 카드 비트맵 만드는 곳
     *
     * @param imageBitmap 카드에 들어갈 인증 사진 비트맵
     *
     * @return 카드 이미지 비트맵
     */
    private fun makeCardBitmap(imageBitmap: Bitmap): Bitmap {

        // 카드 여백
        val margin = 10f.scale()

        // 펫프렌즈 로고 크기
        val logoWidth = 80f.scale()
        val logoHeight = 11f.scale()

        // 뒷 배경 카드 크기
        val cardWidth = 237f.scale()
        val cardHeight = 337f.scale()

        // 펫프렌즈 로고
        val logo = BitmapFactory.decodeResource(resources, R.drawable.img_logo).let {
            Bitmap.createScaledBitmap(it, logoWidth.toInt(), logoHeight.toInt(), false)
        }

        // 캔버스 전체 크기
        val canvasWidth = cardWidth.toInt()
        val canvasHeight = (logoHeight + margin + cardHeight).toInt()

        // 캔버스 그리기
        val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {

            /* 사각형 좌표

            (left, top)
                 o----------+
                 |          |
                 |          |
                 |          |
                 |          |
                 |          |
                 +----------o
                      (right, bottom)
             */

            // 펫프렌즈 로고
            drawBitmap(logo, 0.0f, 0.0f, null)

            // 카드 좌표
            val cardTop = logoHeight + margin

            // 배경 사각형
            drawRoundRect(
                0.0f,
                cardTop,
                cardWidth,
                cardHeight,
                cornerRadius,
                cornerRadius,
                Paint().apply {
                    isAntiAlias = true
                    color = Color.parseColor("#FFFFFF")
                }
            )

            // 이미지 좌표
            val imageTop = cardTop + margin
            val imageRight = margin + imageSize
            val imageBottom = imageTop + imageSize

            val backgroundBitmap = Bitmap.createScaledBitmap(imageBitmap, 2048, 2048, false).let {
                Bitmap.createBitmap(it, (2048 / 2) - (720 / 2), (2048 / 2) - (1024 / 2), 720, 1280)
                    .blur(this@BitmapCombineActivity)
            }

            binding.imageBackground.setImageBitmap(backgroundBitmap)

            // 인증 사진
            drawBitmap(
                imageBitmap,
                margin,
                imageTop,
                null
            )

            // 그냥 텍스트
            val text = "냥치 열띠미해서\n건치될거다옹! 😬"
            val strings = text.split("\n")

            val textPaint = Paint().apply {
                isAntiAlias = true
                textSize = 16f.scale()
                typeface = Typeface.create(ResourcesCompat.getFont(this@BitmapCombineActivity, R.font.noto_sans_kr_bold), Typeface.BOLD)
            }

            val textTop = imageBottom + 16f.scale() + textPaint.descent().scale()
            var textBottom = 0.0f

            for ((index, string) in strings.withIndex()) {
                textBottom = textTop + textPaint.descent().scale() * index + 8f.scale() * index + textPaint.descent().scale()
                drawText(
                    string,
                    margin,
                    textTop + textPaint.descent().scale() * index + 8f.scale() * index,
                    textPaint
                )
            }

            // 날짜 텍스트
            val dateText = "2021. 06. 12 오후 11:50"
            val dateTextPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#888888")
                textSize = 10f.scale()
                typeface = Typeface.create(ResourcesCompat.getFont(this@BitmapCombineActivity, R.font.noto_sans_kr_regular), Typeface.NORMAL)
            }

            drawText(
                dateText,
                margin,
                textBottom + 4f.scale(),
                dateTextPaint
            )
        }

        try {
            val out = FileOutputStream(cardFilePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    /**
     * 배경 만드는 곳
     *
     * @param imageBitmap 카드에 들어갈 인증 사진 비트맵
     *
     * @return 카드 이미지 비트맵
     */
    private fun makeBackgroundBitmap(imageBitmap: Bitmap): Bitmap {

        val smallWidth = 720 / 3
        val smallHeight = 1280 / 3

        val bitmap = Bitmap.createBitmap(
            imageBitmap,
            (imageBitmap.width / 2) - (smallWidth / 2),
            (imageBitmap.height / 2) - (smallHeight / 2),
            smallWidth,
            smallHeight
        ).let {
            Bitmap.createScaledBitmap(it, 720, 1280, false)
        }

        bitmap.blur(this@BitmapCombineActivity)

        try {
            val out = FileOutputStream(bgFilePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }
}

fun Float.scale(): Float {
    return this * 3;
}

fun Int.scale(): Int {
    return this * 3
}

fun Bitmap.blur(context: Context, radius: Float = 25f): Bitmap {
    val bitmap = copy(config, true)

    for(i in 0 until 10) {
        RenderScript.create(context).apply {
            val input = Allocation.createFromBitmap(this, this@blur)
            val output = Allocation.createFromBitmap(this, this@blur)

            ScriptIntrinsicBlur.create(this, Element.U8_4(this)).apply {
                setInput(input)
                setRadius(radius)
                forEach(output)

                output.copyTo(bitmap)
                destroy()
            }
        }
    }

    return bitmap
}