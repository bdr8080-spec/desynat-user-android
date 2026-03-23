package com.desynatuser.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.desynatuser.R
import com.desynatuser.adapter.CountryPickerAdapter
import com.desynatuser.adapter.SelectedCountryPickerAdapter
import com.desynatuser.databinding.AlertDialogLayoutBinding
import com.desynatuser.databinding.CustomCountryPickerBinding
import com.desynatuser.databinding.DeleteDialogLayoutBinding
import com.desynatuser.databinding.LoginAlertDialogLayoutBinding
import com.desynatuser.databinding.SuccessDialogLayoutBinding
import com.desynatuser.ui.auth.AuthActivity
import com.desynatuser.ui.auth.response.GetCountryResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object CommonMethod {

    fun openDatePicker(context: Context,editText: EditText,format:String="dd-MM-yyyy",pastDate:Boolean=false,futureDate:Boolean=true,onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val formattedDate = sdf.format(selectedCalendar.time)

                editText.setText(formattedDate)
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )

        if (!pastDate){
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }
        if (!futureDate) {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }

        datePickerDialog.show()
    }


     fun detectGestureMode(root: View, viewBottom: View) {
         root.setOnApplyWindowInsetsListener { _, insets ->
            val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val navigationBarInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())

            val isUsingNavigationButtons = navigationBarInsets.bottom > 0

            if (isUsingNavigationButtons) {
                viewBottom.isVisible = true
            } else {
                viewBottom.isVisible = false
            }
            insets
        }
    }


    fun uriToFile(context:Context,uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "picked_image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun removeAllTextSpaces (editText: AppCompatEditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val text = charSequence.toString()
                val newText = text.replace(" ", "")
                if (text != newText) {
                    editText.setText(newText)
                    editText.setSelection(newText.length)
                }
            }
            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    fun removeSpace(editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

                if (start == 0 && count > 0 && charSequence?.startsWith(" ") == true) {
                    editText.setText(charSequence.toString().trim())
                    editText.setSelection(editText.text.length)
                }
            }
            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    fun spannableText(
        context: Context,
        fullString: String,
        spannableString: String,
        onClick: (() -> Unit)? = null,
        color: Int = R.color.brown,
        fontRes: Int = R.font.poppins_semi_bold
    ): SpannableString {
        val textSpannable = SpannableString(fullString)

        val typeface = ResourcesCompat.getFont(context, fontRes)

        fun applySpan(target: String, click: (() -> Unit)?) {
            val startIndex = textSpannable.indexOf(target)
            if (startIndex != -1) {
                val endIndex = startIndex + target.length

                typeface?.let {
                    textSpannable.setSpan(
                        CustomTypefaceSpan(null, it),
                        startIndex, endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                textSpannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, color)),
                    startIndex, endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                click?.let {
                    textSpannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                it()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false // no underline
                                ds.color = ContextCompat.getColor(context, color)
                            }
                        },
                        startIndex, endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        applySpan(spannableString, onClick)

        return textSpannable

    }

    fun commonErrorHandling(
        context: Context,
        errorCode:Int,
        errorMessage:String

    ) {
        try {
            when (errorCode) {

                401 -> {
                    clearAllData(context)
                }



            }
        }

        catch (e:Exception){

        }
    }

    fun applyArabicOnlyFilter(editText: EditText) {

        val filter = InputFilter { source, start, end, dest, dstart, dend ->

            // Block English alphabets
            val englishRegex = Regex("[a-zA-Z]")

            if (source != null && source.contains(englishRegex)) {
                return@InputFilter ""
            }

            // Block leading space
            if (dstart == 0 && source.startsWith(" ")) {
                return@InputFilter ""
            }

            return@InputFilter null
        }

        editText.filters = arrayOf(filter)
    }

    fun clearAllData(context: Context){
        /*SharedPreference.get(context).accessToken = ""
        SharedPreference.get(context).language = ""
        SharedPreference.get(context).clearUser()
        Constants.latitude = 0.0
        Constants.longitude = 0.0
        val intent = Intent(context, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)*/
    }

    fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format("%.2f", price)
          //  price.toString()
        }
    }

    fun formatFloatPrice(price: Float): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            String.format("%.2f", price)
          //  price.toString()
        }
    }

    fun formatPriceToReduceLength(amountStr: String?): String {
        if (amountStr.isNullOrBlank()||amountStr=="null") return ""
        return try {
            val amount = amountStr.toDouble()
            when {
                amount >= 1_00_00_000 -> "${amount / 1_00_00_000}Cr"
                amount >= 1_00_000 -> "${amount / 1_00_000}L"
                amount >= 1_000 -> "${amount / 1_000}K"
                else -> {
                    if (amount % 1.0 == 0.0) {
                        amount.toInt().toString()
                    } else {
                        amount.toString()
                    }
                }
            }
        } catch (e: NumberFormatException) {
            amountStr // fallback → return original string if parsing fails
        }
    }


    fun customizeChangeDateFormat(inputDate: String,inputFormat:String="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",outputFormat:String="dd MMM,yyyy"): String {
        val originalFormat: SimpleDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")}
        val targetFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)

        val date: Date
        try {
            date = originalFormat.parse(inputDate)
            return targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun htmlText(text: String): Spanned {
        return HtmlCompat.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    fun formatDate(date:String,input:String="dd/MM/yyyy",output:String="yyyy-MM-dd"):String{
        val inputFormat = SimpleDateFormat(input, Locale.getDefault())
        val outputFormat = SimpleDateFormat(output, Locale.getDefault())
        val date = inputFormat.parse(date)
        return outputFormat.format(date!!)
    }

    fun isStrongValidPassword(password: String): Boolean {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
        val passwordPattern =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!.*_\\-]).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        // letters + digits
        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
        return regex.matches(password)
    }

    fun showSuccessDialog(context: Context,description:String,button:Boolean=false,onDismiss: (() -> Unit)? = null) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = SuccessDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text=description
        dialogBinding.tvOkay.isVisible = button

        dialogBinding.tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()
        if(button==false){
            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }, 1000)
        }
    }

    fun showSuccessWithTimeDialog(context: Context,description:String,button:Boolean=false,time:Long=1000,onDismiss: (() -> Unit)? = null) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = SuccessDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text=description
        dialogBinding.tvOkay.isVisible = button

        dialogBinding.tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()
        if(button==false){
            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }, time)
        }
    }

    fun deleteAlertDialog(
        context: Context,
        description: String,
        onResult: (action: DialogAction) -> Unit
    ) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = DeleteDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text = description

        dialogBinding.btnDelete.setOnClickListener {
            onResult(DialogAction.DELETE)
            dialog.dismiss()
        }
        dialogBinding.btnDiscard.setOnClickListener {
            onResult(DialogAction.DISCARD)
            dialog.dismiss()
        }

        dialog.show()
    }

    enum class DialogAction {
        DELETE, DISCARD
    }

    fun showAlertDialog(context: Context,description:String,onDismiss: (() -> Unit)? = null) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = AlertDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text=description

        dialogBinding.tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()

    }

    fun loginAlertDialog(
        context: Context,
        onResult: (action: String) -> Unit
    ) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = LoginAlertDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.btnLogin.setOnClickListener {
            onResult("yes")
            dialog.dismiss()
        }
        dialogBinding.btnCancel.setOnClickListener {
            onResult("no")
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showLoginAlert(context: Context){
        loginAlertDialog(context){
                action ->
            when (action) {
                "yes" -> {
                    Constants.isGuest = false
                    Constants.isLogout = true
                    Constants.Category = "Women"
                    context.startActivity(
                        Intent(context,
                            AuthActivity::class.java)
                    )
                    (context as? Activity)?.finish()
                }

                "no" -> {

                }
            }
        }
    }


    fun clearData(context: Context){
        SharedPreference.get(context).clearUser()
        SharedPreference.get(context).clearAddress()
        SharedPreference.get(context).accessToken = ""
        Constants.Category = "Women"
    }

    fun clearAffiliateData(context: Context){
        SharedPreference.get(context).clearAffiliateUser()
        SharedPreference.get(context).affiliateAccessToken = ""
    }

    fun shareProduct(context:Context,name: String, desc: String, imageUrl: String) {

        val shareText = """
        Title: $name
        Description: $desc
        Image:
        $imageUrl
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun shareProductWithImage(context: Context, name: String, desc: String, imageUrl: String,link: String) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val url = URL(imageUrl)
                val bitmap = BitmapFactory.decodeStream(url.openStream())

                val file = File(context.cacheDir, "shared_image.png")
                val output = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.close()

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                val shareText = """
                $name
                $desc
                $link
            """.trimIndent()

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, "Share via"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getExpectedDeliveryDate(startDays: Int = 3, endDays: Int = 5,output:String="dd/MM/yyyy"): String {

        val calendar = Calendar.getInstance()

        var workingDaysAdded = 0

        while (workingDaysAdded < endDays) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                workingDaysAdded++
            }
        }

        val sdf = SimpleDateFormat(output, Locale.getDefault())
        val endDate = sdf.format(calendar.time)

        return endDate
    }

    fun downloadPdf(context: Context, pdfUrl: String, fileName: String = "Invoice") {

        try {
            val request = DownloadManager.Request(Uri.parse(pdfUrl))
                .setTitle("Invoice "+fileName)
                .setDescription("Downloading PDF...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.pdf")

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            context.showToast("Download started")

        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Download failed: ${e.message}")
        }
    }

    fun openPdfInBrowser(context: Context, pdfUrl: String) {
        if (pdfUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun formatChatTime(inputTime: String): String {
        val inputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val fullDateFormat = SimpleDateFormat("dd/MM/yy, hh:mm a", Locale.ENGLISH)

        val messageDate = inputFormat.parse(inputTime) ?: return inputTime
        val now = Date()

        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfToday = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startOfYesterday = calendar.time

        return when {
            messageDate.after(startOfToday) -> {
                "Today, ${timeFormat.format(messageDate)}"
            }

            messageDate.after(startOfYesterday) -> {
                "Yesterday, ${timeFormat.format(messageDate)}"
            }

            else -> {
                val diffMillis = now.time - messageDate.time
                val daysDiff = TimeUnit.MILLISECONDS.toDays(diffMillis)

                if (daysDiff in 2..30) {
                    "$daysDiff days ago, ${timeFormat.format(messageDate)}"
                } else {
                    fullDateFormat.format(messageDate)
                }
            }
        }
    }

    fun showUserCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        isDial:Boolean = false,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        binding.rvCountry.adapter = CountryPickerAdapter(context, countryList,isDial,
            object : CountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }

    fun showSelectedCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        isDial:Boolean = false,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        binding.rvCountry.adapter = SelectedCountryPickerAdapter(context, countryList,isDial,
            object : SelectedCountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }

    fun copyText(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        context.showToast("Copied to clipboard")
    }

    fun shareLink(context: Context, link: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link)
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}
