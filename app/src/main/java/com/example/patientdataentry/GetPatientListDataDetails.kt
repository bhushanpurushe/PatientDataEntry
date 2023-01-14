package com.example.patientdataentry

import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageView
import com.example.patientdataentry.database.db.PatientAppDB
import com.example.patientdataentry.database.entity.PatientDataEntity
import com.google.android.material.textfield.TextInputEditText
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class GetPatientListDataDetails : AppCompatActivity() {

    private lateinit var nameLblEditTxt : TextInputEditText
    private lateinit var ageLblEditTxt : TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    lateinit var radioMaleGenderButton: RadioButton
    lateinit var radioFeMaleGenderButton: RadioButton
    lateinit var radioTransGenderButton: RadioButton
    private lateinit var mbNoLblEditTxt : TextInputEditText
    private lateinit var emailLblEditTxt : TextInputEditText

    private lateinit var drNameLblEditTxt : TextInputEditText
    private lateinit var drEmailLblEditTxt : TextInputEditText
    private lateinit var baldnessGradeLblEditTxt : TextInputEditText
    private lateinit var hairTypeLblEditTxt : TextInputEditText
    private lateinit var surgicalPlanSuggLblEditTxt : TextInputEditText
    private lateinit var surgicalPlanOptedLblEditTxt : TextInputEditText
    private lateinit var surgeryDtTiLblEditTxt : TextInputEditText
    private lateinit var surgeryEndDtTiLblEditTxt : TextInputEditText
    private lateinit var value1LblEditTxt : TextInputEditText
    private lateinit var value2LblEditTxt : TextInputEditText

    private lateinit var setProfilePhotoImageView: ImageView
    private lateinit var setBeforeScalpImageView: ImageView
    private lateinit var setBeforeScalpTextView: TextView
    private lateinit var setBeforeLeftScalpImageView: ImageView
    private lateinit var setBeforeLeftScalpTextView: TextView
    private lateinit var setBeforeRightScalpImageView: ImageView
    private lateinit var setBeforeRightScalpTextView: TextView
    private lateinit var setAfterScalpImageView: ImageView
    private lateinit var setUploadScreenshotImageView: ImageView

    private lateinit var patientScalpAfterButton: Button
    private lateinit var patientUploadScreenshotButton: Button
    private lateinit var patientSaveDataButton: Button
    private lateinit var getPatientPdfButton: Button
    private lateinit var getReviewPatientPdfButton: Button

    private var patientAfterScalpPhotoUri : Uri? = null
    private var imagePickedScreenshotUri : Uri? = null

    private var patientGender: String? = ""
    private var photoAbsolutePath: String? = ""
    private var beforeScalpAbsolutePath: String? = ""
    private var beforeLeftScalpAbsolutePath: String? = ""
    private var beforeRightScalpAbsolutePath: String? = ""
    private var afterScalpAbsolutePath: String? = ""
    private var txtId: String? = ""

    private lateinit var patientPhotoBitmap: Bitmap
    private lateinit var beforeScalpBitmap: Bitmap
    private lateinit var beforeLeftScalpBitmap: Bitmap
    private lateinit var beforeRightScalpBitmap: Bitmap
    private lateinit var afterScalpBitmap: Bitmap

    private var patientImage: Image? = null
    private var beforeScalpImage: Image? = null
    private var beforeLeftScalpImage: Image? = null
    private var beforeRightScalpImage: Image? = null
    private var afterScalpImage: Image? = null
    private var screenshotImage: Image? = null

    private val AFTER_SCALP_PERMISSION_REQUEST_CODE = 46
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE")

    private val patientAppDB by lazy { PatientAppDB.getAppDb(this)?.patientdao() }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_patient_list_details)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        nameLblEditTxt = findViewById(R.id.nameSetTextInputEditText)
        ageLblEditTxt = findViewById(R.id.ageSetTextInputEditText)

        genderRadioGroup = findViewById(R.id.radioSetGender)
        val selectedOption: Int = genderRadioGroup.checkedRadioButtonId
        radioMaleGenderButton = findViewById(R.id.radioSetMale)
        radioFeMaleGenderButton = findViewById(R.id.radioSetFemale)
        radioTransGenderButton = findViewById(R.id.radioSetTransgender)

        mbNoLblEditTxt = findViewById(R.id.mobileSetTextInputEditText)
        emailLblEditTxt = findViewById(R.id.emailSetTextInputEditText)
        drNameLblEditTxt = findViewById(R.id.drSetNameTextInputEditText)
        drEmailLblEditTxt = findViewById(R.id.drSetEmailTextInputEditText)
        baldnessGradeLblEditTxt = findViewById(R.id.baldnessGradeSetTextInputEditText)
        hairTypeLblEditTxt = findViewById(R.id.hairTypeSetTextInputEditText)
        surgicalPlanSuggLblEditTxt = findViewById(R.id.surgicalPlanSuggSetTextInputEditText)
        surgicalPlanOptedLblEditTxt = findViewById(R.id.surgicalPlanOptedSetTextInputEditText)
        surgeryDtTiLblEditTxt = findViewById(R.id.surgeryDtTiTextSetInputEditText)
        value1LblEditTxt = findViewById(R.id.value1TextInputEditText1)
        value2LblEditTxt = findViewById(R.id.value2TextInputEditText2)
        surgeryEndDtTiLblEditTxt = findViewById(R.id.surgeryEndDtTiTextSetInputEditText)

        setProfilePhotoImageView = findViewById(R.id.set_profile_imageView)
        setBeforeScalpImageView = findViewById(R.id.set_before_scalp_imageView)
        setBeforeScalpTextView = findViewById(R.id.front_scalp_textView)

        setBeforeLeftScalpImageView = findViewById(R.id.set_before_leftscalp_imageView)
        setBeforeLeftScalpTextView = findViewById(R.id.left_scalp_textView)
        setBeforeRightScalpImageView = findViewById(R.id.set_before_rightscalp_imageView)
        setBeforeRightScalpTextView = findViewById(R.id.right_scalp_textView)

        setAfterScalpImageView = findViewById(R.id.get_after_scalp_imageView)
        setUploadScreenshotImageView = findViewById(R.id.get_upload_screenshots_imageView)

        patientScalpAfterButton = findViewById(R.id.scalp_after_photo_button)
        patientUploadScreenshotButton = findViewById(R.id.upload_screenshot_button)
        patientSaveDataButton = findViewById(R.id.get_save_button)
        getPatientPdfButton = findViewById(R.id.get_pdf_button)
        getReviewPatientPdfButton = findViewById(R.id.review_pdf_button)

        val bundle :Bundle ?=intent.extras
        val patientId = bundle!!.getInt("patient_id")
        Log.v("patientId", patientId.toString())

        txtId = Random(System.currentTimeMillis()).nextInt(99999).toString()
        Log.v("txtId", txtId.toString())

        surgeryEndDtTiLblEditTxt.setText(getCurrentDate())

        lifecycleScope.launch {
            try {

                patientAppDB?.getPatientSingleData(patientId)?.collect { patientData ->

                    Log.v("patientName", patientData.patient_name)
                    Log.v("docName", patientData.doctor_name)
                    Log.v("gender", patientData.patient_gender)

                    nameLblEditTxt.setText(patientData.patient_name)
                    ageLblEditTxt.setText(patientData.patient_age.toString())

                    if (patientData.patient_gender == getString(R.string.radio_male)){
                        radioMaleGenderButton.isChecked = true
                        radioFeMaleGenderButton.isChecked = false
                        radioTransGenderButton.isChecked = false
                        patientGender = getString(R.string.radio_male)
                    }else if (patientData.patient_gender == getString(R.string.radio_female)){
                        radioMaleGenderButton.isChecked = false
                        radioFeMaleGenderButton.isChecked = true
                        radioTransGenderButton.isChecked = false
                        patientGender = getString(R.string.radio_female)
                    }else if (patientData.patient_gender == getString(R.string.radio_transgender)){
                        radioMaleGenderButton.isChecked = false
                        radioFeMaleGenderButton.isChecked = false
                        radioTransGenderButton.isChecked = true
                        patientGender = getString(R.string.radio_transgender)
                    }else{
                        Log.v("gender", patientData.patient_gender)
                    }

                    mbNoLblEditTxt.setText(patientData.patient_mbno)
                    emailLblEditTxt.setText(patientData.patient_email)
                    drNameLblEditTxt.setText(patientData.doctor_name)
                    drEmailLblEditTxt.setText(patientData.doctor_email)
                    baldnessGradeLblEditTxt.setText(patientData.patient_baldness_grade)
                    hairTypeLblEditTxt.setText(patientData.patient_hair_type)
                    surgicalPlanSuggLblEditTxt.setText(patientData.patient_surg_plan_sugg)
                    surgicalPlanOptedLblEditTxt.setText(patientData.patient_surg_plan_opted)
                    surgeryDtTiLblEditTxt.setText(patientData.patient_surg_datetime)

                    photoAbsolutePath = patientData.patient_photo
                    beforeScalpAbsolutePath = patientData.patient_before_scalp_photo
                    beforeLeftScalpAbsolutePath = patientData.patient_before_left_scalp_photo
                    beforeRightScalpAbsolutePath = patientData.patient_before_right_scalp_photo

                    val patientPhotoFile = File(patientData.patient_photo)
                    val patientBeforeScalpPhotoFile = File(patientData.patient_before_scalp_photo)
                    val patientBeforeLeftScalpPhotoFile = File(patientData.patient_before_left_scalp_photo)
                    val patientBeforeRightScalpPhotoFile = File(patientData.patient_before_right_scalp_photo)

                    if (patientPhotoFile.length() > 0) {
                        val patientProfilePhoto = BitmapFactory.decodeFile(patientData.patient_photo)
                        setProfilePhotoImageView.visibility = View.VISIBLE
                        setProfilePhotoImageView.setImageBitmap(patientProfilePhoto)
                    } else {
                        Log.v("patientPhotoFile", "patientPhotoFile is null or empty")
                    }

                    if (patientBeforeScalpPhotoFile.length() > 0) {
                        val patientBeforeScalpPhoto = BitmapFactory.decodeFile(patientData.patient_before_scalp_photo)
                        setBeforeScalpImageView.visibility = View.VISIBLE
                        setBeforeScalpTextView.visibility = View.VISIBLE
                        setBeforeScalpImageView.setImageBitmap(patientBeforeScalpPhoto)
                    } else {
                        Log.v("beforeScalpFile", "patientBeforeScalpPhotoFile is null or empty")
                    }

                    if (patientBeforeLeftScalpPhotoFile.length() > 0) {
                        val patientBeforeLeftScalpPhoto = BitmapFactory.decodeFile(patientData.patient_before_left_scalp_photo)
                        setBeforeLeftScalpImageView.visibility = View.VISIBLE
                        setBeforeLeftScalpTextView.visibility = View.VISIBLE
                        setBeforeLeftScalpImageView.setImageBitmap(patientBeforeLeftScalpPhoto)
                    } else {
                        Log.v("beforeScalpFile", "patientBeforeScalpPhotoFile is null or empty")
                    }

                    if (patientBeforeRightScalpPhotoFile.length() > 0) {
                        val patientBeforeRightScalpPhoto = BitmapFactory.decodeFile(patientData.patient_before_right_scalp_photo)
                        setBeforeRightScalpImageView.visibility = View.VISIBLE
                        setBeforeRightScalpTextView.visibility = View.VISIBLE
                        setBeforeRightScalpImageView.setImageBitmap(patientBeforeRightScalpPhoto)
                    } else {
                        Log.v("beforeScalpFile", "patientBeforeScalpPhotoFile is null or empty")
                    }
                }

            }catch (e:Exception){
                Toast.makeText(this@GetPatientListDataDetails, "Record Not Found ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        patientScalpAfterButton.setOnClickListener {

            val photoAfterScalpFile = File.createTempFile("IMG_", ".jpg",
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

            patientAfterScalpPhotoUri = FileProvider.getUriForFile(applicationContext,
                "${applicationContext.packageName}.provider", photoAfterScalpFile)

            afterScalpAbsolutePath = photoAfterScalpFile.absolutePath
            Log.e("photo-absolutePath", afterScalpAbsolutePath.toString())
            val name: String = photoAfterScalpFile.name
            Log.e("photo-name", name)
            val nameWithoutExtension: String = photoAfterScalpFile.nameWithoutExtension
            Log.e("photo-nameWithoutExt", nameWithoutExtension)
            val path: String = photoAfterScalpFile.path
            Log.e("photo-path", path)

            val isAllowPermission = allPermissionsGranted()
            Log.e("abx", "Permission Granted $isAllowPermission")
            if (!isAllowPermission) {
                requestPermissions(REQUIRED_PERMISSIONS, AFTER_SCALP_PERMISSION_REQUEST_CODE)
            } else {
                Log.e("abx", "Already Permission Granted ")
                takepatientAterScalpPhotoPicture.launch(patientAfterScalpPhotoUri)
            }
        }

        patientUploadScreenshotButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        patientSaveDataButton.setOnClickListener {

            val getNameTxtInpEditTxt = nameLblEditTxt.text.toString()
            Log.v("Patient Name", getNameTxtInpEditTxt)
            val getAgeTxtInpEditTxt = ageLblEditTxt.text.toString()
            Log.v("Age", getAgeTxtInpEditTxt)
            val getMbNoTxtInpEditTxt = mbNoLblEditTxt.text.toString()
            val getEmailTxtInpEditTxt = emailLblEditTxt.text.toString()
            val getDocNameTxtInpEditTxt = drNameLblEditTxt.text.toString()
            val getDocEmailTxtInpEditTxt = drEmailLblEditTxt.text.toString()
            val getBaldnessTxtInpEditTxt = baldnessGradeLblEditTxt.text.toString()
            val getHairTypeTxtInpEditTxt = hairTypeLblEditTxt.text.toString()
            val getSurgPlanSuggTxtInpEditTxt = surgicalPlanSuggLblEditTxt.text.toString()
            val getSurgPlanOptTxtInpEditTxt = surgicalPlanOptedLblEditTxt.text.toString()
            val getSurDtTiTxtInpEditTxt = surgeryDtTiLblEditTxt.text.toString()
            val getValue1TxtInpEditTxt = value1LblEditTxt.text.toString()
            Log.v("Value1", getValue1TxtInpEditTxt)
            val getValue2TxtInpEditTxt = value2LblEditTxt.text.toString()
            Log.v("Value2", getValue2TxtInpEditTxt)

            val getSurEndDtTiTxtInpEditTxt = surgeryEndDtTiLblEditTxt.text.toString()

            val afterScalpPhotoFile = File(afterScalpAbsolutePath)

            if (afterScalpAbsolutePath == "" || afterScalpPhotoFile.length() == 0.toLong()){
                Toast.makeText(this@GetPatientListDataDetails, "Please capture After Scalp Photo", Toast.LENGTH_SHORT).show()
            } else if (getValue1TxtInpEditTxt.isBlank()){
                value1LblEditTxt.setError("Value 1 Should not be blank")
                Toast.makeText(this@GetPatientListDataDetails, "Value 1 Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (getValue2TxtInpEditTxt.isBlank()){
                value2LblEditTxt.setError("Value 2 Should not be blank")
                Toast.makeText(this@GetPatientListDataDetails, "Value 2 Should not be blank", Toast.LENGTH_SHORT).show()
            } else if (setUploadScreenshotImageView.drawable == null){
                Toast.makeText(this@GetPatientListDataDetails, "Please upload screenshot", Toast.LENGTH_SHORT).show()
            }else{

                lifecycleScope.launch {

                    var fileName1 = ""
                    val document = Document()

                    fileName1 = "$getNameTxtInpEditTxt$txtId.pdf"
                    Log.v("fileName11", fileName1)
                    val dest: String = this@GetPatientListDataDetails.getExternalFilesDir(null).toString() + "/"
                    val dir = File(dest)
                    if (!dir.exists()) dir.mkdirs()

                    try {
                        val file = File(dest, fileName1)
                        file.createNewFile()
                        val fOut = FileOutputStream(file, false)
                        val writer = PdfWriter.getInstance(document, fOut)

                        document.open()

                        val patientPhotoFile = File(photoAbsolutePath)
                        val beforeScalpPhotoFile = File(beforeScalpAbsolutePath)
                        val beforeLeftScalpPhotoFile = File(beforeLeftScalpAbsolutePath)
                        val beforeRightScalpPhotoFile = File(beforeRightScalpAbsolutePath)
                        val afterScalpPhotoFile1 = File(afterScalpAbsolutePath)

                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))

                        if (patientPhotoFile.length() > 0) {
                            patientPhotoBitmap = BitmapFactory.decodeFile(photoAbsolutePath)
                            patientImage = patientPhotoBitmap.let { convertBitmapToByteArray1(it) }

                            val table = PdfPTable(2)
                            table.widthPercentage = 100f

                            val columnWidths = floatArrayOf(60f, 40f)
                            table.setWidths(columnWidths)

                            table.addCell(createTextCell("Patient Name :- $getNameTxtInpEditTxt\nPatient Age  :- $getAgeTxtInpEditTxt \nPatient Gender :- $patientGender \nPatient Mobile Number :- $getMbNoTxtInpEditTxt\nPatient Email-Id :- $getEmailTxtInpEditTxt "))
                            table.addCell(createImageCell(patientImage))

                            document.add(table)
                            /*document.add(patientImage)
                            document.newPage()*/
                        } else {
                            Log.v("patientPhotoFile", "patientPhotoFile is null or empty")
                        }

                        val table1 = PdfPTable(1)
                        table1.widthPercentage = 100f

                        table1.addCell(createBottomTextCell("Doctor Name :- $getDocNameTxtInpEditTxt\nDoctor Email-Id :- $getDocEmailTxtInpEditTxt" +
                                "\nBaldness Grade :-  $getBaldnessTxtInpEditTxt\nHair Type :- $getHairTypeTxtInpEditTxt" +
                                "\nSurgical Plan Suggested :- $getSurgPlanSuggTxtInpEditTxt\nSurgical Plan Opted $getSurgPlanOptTxtInpEditTxt" +
                                "\nSurgery Date and Time $getSurDtTiTxtInpEditTxt\nGrafts Extracted :- $getValue1TxtInpEditTxt\nGrafts Implanted :- $getValue2TxtInpEditTxt\n" +
                                "Surgery End Date and Time :- $getSurEndDtTiTxtInpEditTxt"))

                        document.add(table1)
                        document.newPage()

//                        document.add(Paragraph("Patient Name :- $getNameTxtInpEditTxt"))
//                        document.add(Paragraph("Patient Age  :- $getAgeTxtInpEditTxt"))
//                        document.add(Paragraph("Patient Gender :- $patientGender"))
//                        document.add(Paragraph("Patient Mobile Number :- $getMbNoTxtInpEditTxt"))
//                        document.add(Paragraph("Patient Email-Id :- $getEmailTxtInpEditTxt"))
//                        document.add(Paragraph("Doctor Name :- $getDocNameTxtInpEditTxt"))
//                        document.add(Paragraph("Doctor Email-Id :- $getDocEmailTxtInpEditTxt"))
//                        document.add(Paragraph("Baldness Grade :-  $getBaldnessTxtInpEditTxt"))
//                        document.add(Paragraph("Hair Type :- $getHairTypeTxtInpEditTxt"))
//                        document.add(Paragraph("Surgical Plan Suggested :- $getSurgPlanSuggTxtInpEditTxt"))
//                        document.add(Paragraph("Surgical Plan Opted $getSurgPlanOptTxtInpEditTxt"))
//                        document.add(Paragraph("Surgery Date and Time $getSurDtTiTxtInpEditTxt"))
//                        document.add(Paragraph("Value 1 :- $getValue1TxtInpEditTxt"))
//                        document.add(Paragraph("Value 2 :- $getValue2TxtInpEditTxt"))

                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))

                        val table11 = PdfPTable(2)
                        table11.widthPercentage = 100f

                        val columnWidths11 = floatArrayOf(50f, 50f)
                        table11.setWidths(columnWidths11)

                        table11.addCell(createScalpTextCell("Left Scalp Photo"))
                        table11.addCell(createScalpTextCell("Front Scalp Photo"))

                        document.add(table11)

                        val table2 = PdfPTable(2)
                        table2.widthPercentage = 100f

                        val columnWidths2 = floatArrayOf(50f, 50f)
                        table2.setWidths(columnWidths2)

                        if (beforeLeftScalpPhotoFile.length() > 0){
                            beforeLeftScalpBitmap = BitmapFactory.decodeFile(beforeLeftScalpAbsolutePath)
                            beforeLeftScalpImage = beforeLeftScalpBitmap.let { convertBitmapToByteArray(it,250F,250F) }

                            table2.addCell(createImageCellForScalp(beforeLeftScalpImage))
                            /*document.add(beforeLeftScalpImage)
                            document.newPage()*/
                        } else {
                            Log.v("beforeScalpPhotoFile", "beforeScalpPhotoFile is null or empty")

                        }

                        if (beforeScalpPhotoFile.length() > 0){
                            beforeScalpBitmap = BitmapFactory.decodeFile(beforeScalpAbsolutePath)
                            beforeScalpImage = beforeScalpBitmap.let { convertBitmapToByteArray(it,250F,250F) }

                            table2.addCell(createImageCellForScalp(beforeScalpImage))
                           /* document.add(beforeScalpImage)
                            document.newPage()*/
                        } else {
                            Log.v("beforeScalpPhotoFile", "beforeScalpPhotoFile is null or empty")
                        }

                        document.add(table2)

                        val table21 = PdfPTable(2)
                        table21.widthPercentage = 100f

                        val columnWidths21 = floatArrayOf(50f, 50f)
                        table21.setWidths(columnWidths21)

                        table21.addCell(createScalpTextCell("Right Scalp Photo"))
                        table21.addCell(createScalpTextCell("After Surgery Scalp Photo"))

                        document.add(table21)

                        val table3 = PdfPTable(2)
                        table3.widthPercentage = 100f

                        val columnWidths3 = floatArrayOf(50f, 50f)
                        table3.setWidths(columnWidths3)

                        if (beforeRightScalpPhotoFile.length() > 0){
                            beforeRightScalpBitmap = BitmapFactory.decodeFile(beforeRightScalpAbsolutePath)
                            beforeRightScalpImage = beforeRightScalpBitmap.let { convertBitmapToByteArray(it,250F,250F) }

                            table3.addCell(createImageCellForScalp(beforeRightScalpImage))
                            /*document.add(beforeRightScalpImage)
                            document.newPage()*/
                        } else {
                            Log.v("beforeScalpPhotoFile", "beforeScalpPhotoFile is null or empty")
                        }

                        if (afterScalpPhotoFile1.length() > 0){
                            afterScalpBitmap = BitmapFactory.decodeFile(afterScalpAbsolutePath)
                            afterScalpImage = afterScalpBitmap.let { convertBitmapToByteArray(it,250F,250F) }

                            table3.addCell(createImageCellForScalp(afterScalpImage))
                            /*document.add(afterScalpImage)
                            document.newPage()*/
                        } else {
                            Log.v("afterScalpPhotoFile", "afterScalpPhotoFile is null or empty")
                        }

                        document.add(table3)
                        document.newPage()

                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))
                        document.add(Paragraph(" "))

                        val table4 = PdfPTable(2)
                        table4.widthPercentage = 100f

                        val columnWidths4 = floatArrayOf(50f, 50f)
                        table4.setWidths(columnWidths4)

                        if (imagePickedScreenshotUri != null && !Uri.EMPTY.equals(imagePickedScreenshotUri)) {
                            Log.v("pickedScreenpUri", "Available")
                            val ScreenshotBitmap = getBitmap(contentResolver, imagePickedScreenshotUri)
                            screenshotImage = ScreenshotBitmap?.let { convertBitmapToByteArray2(it,500F,500F) }

                            table4.addCell(createImageCellForScalp(screenshotImage))
                            table4.addCell(createImageCellForScalp(null))
                          /*  document.add(screenshotImage)
                            document.newPage()*/
                        } else {
                            Log.v("pickedScreenpUri", "is null or empty")
                        }

                        document.add(table4)
                        document.close()

                    } catch (e: DocumentException) {
                        e.printStackTrace()
                        Log.v("PdfError", e.toString())
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        Log.v("PdfError", e.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.v("PdfError", e.toString())
                    }

                    Log.v("fileName12", fileName1)
                    var pdfFilePath = "$dest/$fileName1"
                    Log.v("pdfFilePath", pdfFilePath)

                    val patientDataItem = PatientDataEntity(patientId,"",
                        getNameTxtInpEditTxt ?: "", getAgeTxtInpEditTxt.toInt() ?: 0, patientGender ?: "",
                        getMbNoTxtInpEditTxt ?: "", getEmailTxtInpEditTxt ?: "",photoAbsolutePath ?: "",
                        getDocNameTxtInpEditTxt ?: "", getDocEmailTxtInpEditTxt ?: "",
                        getBaldnessTxtInpEditTxt ?: "", getHairTypeTxtInpEditTxt ?: "",
                        getSurgPlanSuggTxtInpEditTxt ?: "", getSurgPlanOptTxtInpEditTxt ?: "",
                        beforeScalpAbsolutePath ?: "",beforeLeftScalpAbsolutePath ?: "",
                        beforeRightScalpAbsolutePath ?: "", afterScalpAbsolutePath ?: "",
                        getSurDtTiTxtInpEditTxt ?: "", getValue1TxtInpEditTxt ?: "", getValue2TxtInpEditTxt ?: "",
                        imagePickedScreenshotUri.toString()?: "", pdfFilePath ?: "",
                        getSurEndDtTiTxtInpEditTxt)

                    patientAppDB?.update(patientDataItem)

                    setAfterScalpImageView.setImageURI(null)
                    setAfterScalpImageView.visibility = View.GONE
                    value1LblEditTxt.setText("")
                    value2LblEditTxt.setText("")
                    setUploadScreenshotImageView.setImageURI(null)
                    setUploadScreenshotImageView.visibility = View.GONE

                    patientSaveDataButton.setBackgroundColor(ContextCompat.getColor(this@GetPatientListDataDetails, R.color.white))
                    patientSaveDataButton.isEnabled = false
                    getPatientPdfButton.visibility = View.VISIBLE
                    getReviewPatientPdfButton.visibility = View.VISIBLE
                }
            }
        }

        getReviewPatientPdfButton.setOnClickListener {

            lifecycleScope.launch {
                patientAppDB?.getPatientSingleData(patientId)?.collect { patientData ->

                    val pdfFile = File(patientData.patient_pdf_path)
                    if (!pdfFile.exists()) {
                        pdfFile.mkdir()
                    }
                    if (pdfFile != null && pdfFile.exists()) {

                        val intent = Intent(Intent.ACTION_VIEW)
                        val mURI: Uri = FileProvider.getUriForFile(this@GetPatientListDataDetails,
                            this@GetPatientListDataDetails.getApplicationContext().getPackageName().toString() + ".provider", pdfFile)
                        intent.setDataAndType(mURI, "application/pdf")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        try {
                            this@GetPatientListDataDetails.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@GetPatientListDataDetails, "The file choser does not exists! ", Toast.LENGTH_SHORT).show()
                        }

                    }else {
                        Toast.makeText(this@GetPatientListDataDetails, "The file not exists! ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        getPatientPdfButton.setOnClickListener {

            lifecycleScope.launch {
                patientAppDB?.getPatientSingleData(patientId)?.collect { patientData ->

                    val pdfFile = File(patientData.patient_pdf_path)
                    if (!pdfFile.exists()) {
                        pdfFile.mkdir()
                    }
                    if (pdfFile != null && pdfFile.exists()) { //Checking for the file is exist or not

                        val emailIntent = Intent(Intent.ACTION_SEND)
                        val mURI: Uri = FileProvider.getUriForFile(this@GetPatientListDataDetails,
                            this@GetPatientListDataDetails.getApplicationContext().getPackageName().toString() + ".provider", pdfFile)
                        intent.setDataAndType(mURI, "application/pdf")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        try {
                            this@GetPatientListDataDetails.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        /*val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.type = "text/plain"
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "body text")*/

                        val emailToList = arrayOf(patientData.patient_email, patientData.doctor_email)
                        val ccEmailToList = arrayOf("bhushanpurushe@gmail.com")
                        //val bccEmailToList = arrayOf("gauravashtikar123@gmail.com")

                        composeEmail(emailToList, ccEmailToList, /*bccEmailToList,*/
                            "Patient Info PDF ${patientData.patient_name}", mURI)

                        /*emailToList.add(patientData.patient_email)
                        emailToList.add(patientData.doctor_email)
                        emailToList.add("gauravashtikar123@gmail.com")
                        emailToList.add("bhushanpurushe@gmail.com")*/

                    } else {
                        Toast.makeText(this@GetPatientListDataDetails, "The file not exists! ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun composeEmail(addresses: Array<String>, cCAddresses: Array<String>/*, bCCAddresses: Array<String>*/, subject: String, attachment: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_CC, cCAddresses)
            /*putExtra(Intent.EXTRA_BCC, bCCAddresses)*/
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_STREAM, attachment)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private val takepatientAterScalpPhotoPicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
       if (isSaved) {
           Log.e("abx", "aserfd $isSaved")
           invokeAltertDialogForCropping(patientAfterScalpPhotoUri, setAfterScalpImageView, "PatientAfterScalpPhoto")
       }
    }

     var pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uriResult ->
             uriResult?.let {
                 Log.e("pickImage", "Image picked " + uriResult)
                 imagePickedScreenshotUri = uriResult
                 Log.e("pickImage", "Image picked " + imagePickedScreenshotUri.toString())
                 setUploadScreenshotImageView.visibility = View.VISIBLE
                 setUploadScreenshotImageView.setImageURI(imagePickedScreenshotUri)
                 //setCrooppedImageView.setImageURI(Uri.parse("content://media/external/images/media/80831"))
                 //set this URI to imageview then get bitmap or get directly bitmap store in PDF
             }
     }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AFTER_SCALP_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("abx", "Already Permission Granted ")
                //if (permissionDocumentType == 2) {
                takepatientAterScalpPhotoPicture.launch(patientAfterScalpPhotoUri)
            }
            return
        }
    }

    fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception){
            null
        }
    }

    private lateinit var byteArray: ByteArray
    private fun convertBitmapToByteArray(bitmap: Bitmap, newBitmapWidth : Float, newBitmapHeight : Float): Image {
        val width: Int = bitmap.getWidth()
        val height: Int = bitmap.getHeight()
        // GET SCALE SIZE
        val scaleWidth = newBitmapWidth / width
        val scaleHeight = newBitmapHeight / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)

        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream)
        byteArray = stream.toByteArray()
        byteArray = byteArray.copyOf(256)

        val image = Image.getInstance(stream.toByteArray())
        //image.scaleAbsolute(200F, 200F)
        //image.scalePercent(22F)
        image.alignment = Element.ALIGN_LEFT

        return image
    }

    private lateinit var byteArray1: ByteArray
    private fun convertBitmapToByteArray1(bitmap: Bitmap): Image {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream)
        byteArray1 = stream.toByteArray()
        byteArray1 = byteArray1.copyOf(256)

        val image = Image.getInstance(stream.toByteArray())
        image.scalePercent(25F)
        image.alignment = Element.ALIGN_RIGHT

        return image
    }

    private lateinit var byteArray2: ByteArray
    private fun convertBitmapToByteArray2(bitmap: Bitmap, newBitmapWidth : Float, newBitmapHeight : Float): Image {
        val width: Int = bitmap.getWidth()
        val height: Int = bitmap.getHeight()
        // GET SCALE SIZE
        val scaleWidth = newBitmapWidth / width
        val scaleHeight = newBitmapHeight / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)
        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)

        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        byteArray2 = stream.toByteArray()
        byteArray2 = byteArray2.copyOf(256)

        val image = Image.getInstance(stream.toByteArray())
        //image.scaleAbsolute(200F, 200F)
        //image.scalePercent(22F)
        image.alignment = Element.ALIGN_LEFT

        return image
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(DocumentException::class, IOException::class)
    fun createTextCell(text: String?): PdfPCell {
        val cell = PdfPCell()
        val p = Paragraph(text)
        p.alignment = Element.ALIGN_LEFT
        cell.addElement(p)
        cell.verticalAlignment = Element.ALIGN_TOP
        cell.border = Rectangle.BOX
        cell.paddingLeft = 15F
        return cell
    }

    @Throws(DocumentException::class, IOException::class)
    fun createScalpTextCell(text: String?): PdfPCell {
        val cell = PdfPCell()
        val p = Paragraph(text)
        p.alignment = Element.ALIGN_LEFT
        cell.addElement(p)
        cell.verticalAlignment = Element.ALIGN_TOP
        cell.border = Rectangle.BOX
        cell.paddingLeft = 5F
        cell.paddingBottom = 10F
        return cell
    }

    @Throws(DocumentException::class, IOException::class)
    fun createBottomTextCell(text: String?): PdfPCell {
        val cell = PdfPCell()
        val p = Paragraph(text)
        p.alignment = Element.ALIGN_LEFT
        cell.addElement(p)
        cell.verticalAlignment = Element.ALIGN_TOP
        cell.border = Rectangle.BOX
        cell.paddingLeft = 15F
        cell.paddingBottom = 15F
        return cell
    }

    @Throws(DocumentException::class, IOException::class)
    fun createImageCell(img: Image?): PdfPCell? {
        var cell = PdfPCell()
        img?.scaleAbsoluteWidth(150f)
        img?.scaleAbsoluteHeight(150f)
        img?.setAlignment(Element.ALIGN_LEFT)
        cell.addElement(img)
        cell.verticalAlignment = Element.ALIGN_TOP
        cell.border = Rectangle.BOX
        return cell
    }

    @Throws(DocumentException::class, IOException::class)
    fun createImageCellForScalp(img: Image?): PdfPCell? {
        var cell = PdfPCell()
        cell.addElement(img)
        //cell = PdfPCell(img, true)
        cell.verticalAlignment = Element.ALIGN_TOP
        cell.border = Rectangle.BOX
        cell.borderColor = BaseColor.BLACK
        return cell
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aaa")
        return sdf.format(Date())
    }

    private fun invokeAltertDialogForCropping(imageUri: Uri?, imageView: ImageView, imagePointsTo : String) {

        val dialog = Dialog(this@GetPatientListDataDetails)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_alter_dialog)
        dialog.getWindow()?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setTitle("Crop Image")
        dialog.setCancelable(false)

        val cropImage = dialog.findViewById(R.id.dialog_cropImageView) as CropImageView
        val cropButton = dialog.findViewById(R.id.dialog_crop_image_button) as Button

        cropImage.setImageUriAsync(imageUri)

        cropButton.setOnClickListener {
            val mCroppedPhotoBitmap : Bitmap? = cropImage.croppedImage

            imageView.visibility = View.VISIBLE
            imageView.setImageBitmap(mCroppedPhotoBitmap)

            val getProfilePhotoPath : String? = getAbsolutePathOfBitmapFile(mCroppedPhotoBitmap)
            Log.e("ImageCheck", "Profile Photo $getProfilePhotoPath")

            when(imagePointsTo){
                "PatientAfterScalpPhoto" -> {
                    Log.e("ImageCheck", "x == previous $afterScalpAbsolutePath")
                    afterScalpAbsolutePath = getProfilePhotoPath
                    Log.e("ImageCheck", "x == Now $afterScalpAbsolutePath")
                }
                else -> {
                    Log.e("ImageCheck", "x is neither 1 nor 2")
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getAbsolutePathOfBitmapFile(mCroppedPhotoBitmap: Bitmap?): String? {

        var tempFileAbsolutePath: String? = ""

        val bitmapTempFile = File.createTempFile("IMG_", ".jpg",
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

        var tempPhotoUri : Uri? = FileProvider.getUriForFile(applicationContext,
            "${applicationContext.packageName}.provider", bitmapTempFile)

        tempFileAbsolutePath = bitmapTempFile.absolutePath
        Log.e("photo-absolutePath", tempFileAbsolutePath.toString())

        val name: String = bitmapTempFile.name
        Log.e("photo-name", name)

        val tempFile = File.createTempFile("IMG_", ".jpg")
        val bytes = ByteArrayOutputStream()
        mCroppedPhotoBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(bitmapTempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()

        return tempFileAbsolutePath
    }

    /*val pdfFile = File(patientData.patient_pdf_path)
   if (!pdfFile.exists()) {
       pdfFile.mkdir()
   }
   if (pdfFile != null && pdfFile.exists()) { //Checking for the file is exist or not

       val intent = Intent(Intent.ACTION_VIEW)
       val mURI: Uri = FileProvider.getUriForFile(
           this@GetPatientListDataDetails, this@GetPatientListDataDetails.getApplicationContext()
               .getPackageName().toString() + ".provider", pdfFile
       )
       intent.setDataAndType(mURI, "application/pdf")
       intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION)
       try {
           this@GetPatientListDataDetails.startActivity(intent)
       } catch (e: Exception) {
           e.printStackTrace()
       }
   } else {
       Toast.makeText(this@GetPatientListDataDetails, "The file not exists! ", Toast.LENGTH_SHORT).show()
   }*/
}