package com.example.online_food.Activity; // <<< Kiểm tra lại package của bạn

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.online_food.Adapter.DatHangAdapter;
import com.example.online_food.Data.ChiTietGioHang;
import com.example.online_food.Data.MonAn;
import com.example.online_food.R; // <<< Đảm bảo R đúng
import com.example.online_food.databinding.ActivityThanhToanQrBinding; // <<< Kiểm tra lại package binding
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class ThanhToanQRActivity extends AppCompatActivity {

    private ActivityThanhToanQrBinding binding; // Sử dụng ViewBinding

    // Khóa EXTRA để nhận dữ liệu
    public static final String EXTRA_ORDER_ID = "com.example.online_food.Activity.EXTRA_ORDER_ID";
    public static final String EXTRA_TOTAL_newMaDH = "com.example.online_food.Activity.EXTRA_TOTAL_newMaDH";
    public static final String EXTRA_TOTAL_AMOUNT = "com.example.online_food.Activity.EXTRA_TOTAL_AMOUNT";
    public static final String EXTRA_TOTAL_KM = "com.example.online_food.Activity.EXTRA_TOTAL_KM";
    public static final String EXTRA_TOTAL_MaNH = "com.example.online_food.Activity.EXTRA_TOTAL_MaNH";
    public static final String EXTRA_TOTAL_MaKH = "com.example.online_food.Activity.EXTRA_TOTAL_MaKH";
    public static final String EXTRA_TOTAL_phiVC = "com.example.online_food.Activity.EXTRA_TOTAL_phiVC";
    public static final String EXTRA_TOTAL_ptThanhToan = "com.example.online_food.Activity.EXTRA_TOTAL_ptThanhToan";
    public static final String EXTRA_TOTAL_TongTien = "com.example.online_food.Activity.EXTRA_TOTAL_TongTien";
    public static final String EXTRA_TOTAL_ThanhTien = "com.example.online_food.Activity.EXTRA_TOTAL_ThanhTien";
    public static final String EXTRA_TOTAL_thoiGianDatHang = "com.example.online_food.Activity.EXTRA_TOTAL_thoiGianDatHang";

    // --- Cấu hình ZaloPay Sandbox ---
    private static final int ZP_APP_ID_INT = 2553;
    private static final String ZP_APP_ID = String.valueOf(ZP_APP_ID_INT);
    private static final String ZP_KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL"; // Dùng để ký request
    private static final String ZP_KEY2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz"; // Thường dùng xác thực callback (không dùng ở đây)
    private static final String ZP_CREATE_ORDER_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create"; // Endpoint tạo đơn
    private static final String ZP_QUERY_STATUS_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/query"; // Endpoint kiểm tra

    // Biến lưu trữ thông tin đơn hàng
    private String appOrderIdFromIntent; // ID đơn hàng gốc từ app
    private String appTransIdSentToZaloPay; // ID giao dịch duy nhất gửi cho ZaloPay
    private double totalAmount;
    private double ThanhTien;
    private double TongTien;
    private String newMaDH;
    private String maNH;
    private String maKH;
    private double KM;
    private double phiVC;
    private String ptThanhToan;

    private String thoiGianDatHang;

    private String orderDescription;

    // Timer và Polling
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_TIME_MS = 15 * 60 * 1000; // 15 phút
    private Handler pollingHandler;
    private Runnable queryStatusRunnable;
    private static final long POLLING_INTERVAL_MS = 5000; // Kiểm tra mỗi 5 giây
    private boolean shouldPoll = false; // Cờ kiểm soát polling

    private static final String TAG = "ThanhToanActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThanhToanQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Handler cho polling
        pollingHandler = new Handler(Looper.getMainLooper());

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        appOrderIdFromIntent = intent.getStringExtra(EXTRA_ORDER_ID);
        newMaDH = intent.getStringExtra(EXTRA_TOTAL_newMaDH);
        maNH = intent.getStringExtra(EXTRA_TOTAL_MaNH);
        maKH = intent.getStringExtra(EXTRA_TOTAL_MaKH);
        totalAmount = intent.getDoubleExtra(EXTRA_TOTAL_AMOUNT, 0.0);
        ThanhTien = intent.getDoubleExtra(EXTRA_TOTAL_ThanhTien, 0.0);
        KM = intent.getDoubleExtra(EXTRA_TOTAL_KM, 0.0);
        phiVC = intent.getDoubleExtra(EXTRA_TOTAL_phiVC, 0.0);
        ptThanhToan = intent.getStringExtra(EXTRA_TOTAL_ptThanhToan);
        TongTien = intent.getDoubleExtra(EXTRA_TOTAL_TongTien, 0.0);
        thoiGianDatHang = intent.getStringExtra(EXTRA_TOTAL_thoiGianDatHang);
        Log.d(TAG, "App Order ID from Intent: " + appOrderIdFromIntent);

        // Kiểm tra dữ liệu đầu vào
        if (appOrderIdFromIntent == null || totalAmount <= 0) {
            Toast.makeText(this, "Thiếu thông tin đơn hàng hoặc số tiền không hợp lệ!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "App Order ID or Total Amount is missing or invalid in Intent extras.");
            finish();
            return;
        }

        // Chuẩn bị thông tin
        orderDescription = "Payment for order #" + appOrderIdFromIntent;
        generateAppTransId(); // Tạo app_trans_id duy nhất để gửi đi

        // Hiển thị thông tin ban đầu
        displayInitialOrderDetails();

        // Gọi API tạo đơn hàng ZaloPay
        createZaloPayOrderDirectly(appTransIdSentToZaloPay, totalAmount);

        // Xử lý Click vào Hướng dẫn thanh toán
        setupHelpLink();

        // Cài đặt ảnh tĩnh (nếu có)
        setupStaticImages();
    }

    // Tạo app_trans_id duy nhất
    private void generateAppTransId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String datePart = sdf.format(new Date());
        Random rand = new Random();
        String randomPart = String.format(Locale.getDefault(), "%06d", rand.nextInt(99999999));
        appTransIdSentToZaloPay = datePart + "_" + randomPart;
        Log.d(TAG, "Generated app_trans_id to send: " + appTransIdSentToZaloPay);
    }

    // Hiển thị thông tin ban đầu
    private void displayInitialOrderDetails() {
        binding.tvMerchantName.setText("Merchant Demo V2"); // Hoặc tên merchant của bạn
        String formattedAmount = String.format(Locale.getDefault(), "đ%,.0f", totalAmount);
        binding.tvOrderValue.setText(formattedAmount);
        binding.tvPaymentAmount.setText(formattedAmount);
        binding.tvDescription.setText(orderDescription);
        binding.tvTransactionId.setText(appTransIdSentToZaloPay); // Hiển thị app_trans_id
//        binding.tvOrderIdValue.setText(appOrderIdFromIntent); // Hiển thị mã đơn hàng gốc
    }

    // Cập nhật TextView mã giao dịch (hiển thị ZP Trans Token/ID nếu muốn)
    private void updateZaloPayTransactionDisplay(String transactionIdentifier) {
        binding.tvTransactionId.setText(transactionIdentifier);
    }

    // Gọi API tạo đơn hàng ZaloPay
    private void createZaloPayOrderDirectly(String appTransIdToUse, double amountParam) {
        showLoading(true);
        binding.ivQrCode.setImageBitmap(null); // Xóa QR cũ

        long amountLong = (long) amountParam;
        long currentTime = System.currentTimeMillis();
        String appUser = "user123"; // Hoặc định danh người dùng phù hợp
        String items = "[{}]"; // Chuỗi JSON rỗng
        String embedData = "{}"; // Chuỗi JSON rỗng
        String bankCode = "zalopayapp"; // Để ưu tiên mở ZaloPay App

        // Chuỗi dữ liệu để tạo chữ ký
        String dataToSign = ZP_APP_ID + "|" + appTransIdToUse + "|" + appUser + "|" + amountLong + "|" + currentTime + "|" + embedData + "|" + items;
        Log.d(TAG, "Create Order - Data to sign: " + dataToSign);

        try {
            String mac = hmacSha256(ZP_KEY1, dataToSign); // Dùng Key 1
            Log.d(TAG, "Create Order - Generated MAC: " + mac);

            // Tạo Request Body dạng Form URL Encoded
            FormBody formBody = new FormBody.Builder()
                    .add("app_id", ZP_APP_ID)
                    .add("app_user", appUser)
                    .add("app_trans_id", appTransIdToUse)
                    .add("app_time", String.valueOf(currentTime))
                    .add("amount", String.valueOf(amountLong))
                    .add("item", items)
                    .add("description", orderDescription)
                    .add("embed_data", embedData)
                    .add("bank_code", bankCode)
                    .add("mac", mac)
                    .build();

            // Tạo Request OkHttp
            Request request = new Request.Builder()
                    .url(ZP_CREATE_ORDER_ENDPOINT)
                    .post(formBody)
                    .build();

            // Tạo Client OkHttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Gửi yêu cầu bất đồng bộ
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Log.e(TAG, "Create Order Failed (Network): ", e);
                        Toast.makeText(ThanhToanQRActivity.this, "Lỗi kết nối khi tạo đơn hàng ZaloPay.", Toast.LENGTH_SHORT).show();
                        binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert); // Hiển thị lỗi
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String responseBody = response.body() != null ? response.body().string() : null;
                    if (response.body() != null) response.body().close();
                    Log.d(TAG, "Create Order Response: " + responseBody);

                    runOnUiThread(() -> {
                        showLoading(false); // Luôn ẩn loading khi có response
                        if (response.isSuccessful() && responseBody != null) {
                            try {
                                JSONObject zpResponse = new JSONObject(responseBody);
                                int returnCode = zpResponse.getInt("return_code");
                                Log.d(TAG, "ZaloPay Create Order returnCode: " + returnCode);

//                              Tạo Order payment thành công
                                if (returnCode == 1) {
                                    // Lấy dữ liệu VietQR để tạo ảnh QR
                                    String zaloPayQrData = zpResponse.getString("qr_code");
                                    // Lấy ZP Trans Token (có thể dùng để hiển thị hoặc query sau này)
                                    String zpTransToken = zpResponse.getString("zp_trans_token");

                                    Log.d(TAG, "Create Order Success - QR Data (VietQR): " + zaloPayQrData);
                                    Log.d(TAG, "Create Order Success - ZP Trans Token: " + zpTransToken);
                                    Log.d(TAG, "Create Order Success - ZP Trans ID: " + appTransIdSentToZaloPay);

                                    // Cập nhật TextView mã giao dịch (ví dụ: hiển thị ZP Token)
                                    updateZaloPayTransactionDisplay(appTransIdSentToZaloPay);

                                    // Tạo và hiển thị ảnh QR từ dữ liệu VietQR
                                    generateAndDisplayQrCode(zaloPayQrData);

                                    // Bắt đầu timer 15 phút
                                    startCountdownTimer();

                                    // Bắt đầu polling trạng thái
                                    shouldPoll = true;
                                    startPollingOrderStatus(appTransIdToUse); // Dùng app_trans_id đã gửi đi

                                } else { // ZaloPay trả về lỗi logic
                                    String returnMessage = zpResponse.getString("return_message");
                                    Log.e(TAG, "Create Order Failed (ZaloPay Logic Error): " + returnCode + " - " + returnMessage);
                                    Toast.makeText(ThanhToanQRActivity.this, "ZaloPay (Tạo đơn): " + returnMessage, Toast.LENGTH_LONG).show();
                                    binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert); // Hiển thị lỗi
                                }
                            } catch (JSONException e) { // Lỗi parsing JSON
                                Log.e(TAG, "Create Order Failed (JSON Parse): ", e);
                                Toast.makeText(ThanhToanQRActivity.this, "Lỗi xử lý phản hồi tạo đơn ZaloPay.", Toast.LENGTH_SHORT).show();
                                binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert); // Hiển thị lỗi
                            }
                        } else { // Lỗi HTTP
                            Log.e(TAG, "Create Order Failed (HTTP): " + response.code());
                            Toast.makeText(ThanhToanQRActivity.this, "Lỗi tạo đơn ZaloPay ("+ response.code() +").", Toast.LENGTH_SHORT).show();
                            binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert); // Hiển thị lỗi
                        }
                    });
                }
            });
        } catch (Exception e) { // Lỗi khi chuẩn bị request (ví dụ: tạo MAC)
            showLoading(false);
            Log.e(TAG, "Error preparing Create Order request: ", e);
            Toast.makeText(this, "Lỗi tạo yêu cầu đơn hàng.", Toast.LENGTH_SHORT).show();
            binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert); // Hiển thị lỗi
        }
    }

    // Tạo và hiển thị ảnh QR
    private void generateAndDisplayQrCode(String qrDataToEncode) {
        if (qrDataToEncode == null || qrDataToEncode.isEmpty()) {
            binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert);
            Log.e(TAG, "QR Data from ZaloPay is null or empty.");
            Toast.makeText(this, "Không nhận được dữ liệu QR từ ZaloPay", Toast.LENGTH_SHORT).show();
            return;
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            int qrCodeSize = 800; // Kích thước cố định
            Log.d(TAG, "Generating QR with fixed size: " + qrCodeSize);

            BitMatrix bitMatrix = multiFormatWriter.encode(qrDataToEncode, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            if (bitmap != null) {
                binding.ivQrCode.setImageBitmap(bitmap);
                Log.d(TAG, "QR Code generated and set successfully!");
            } else {
                throw new Exception("Generated Bitmap is null"); // Ném lỗi nếu bitmap null
            }
        } catch (WriterException | IllegalArgumentException e) {
            Log.e(TAG, "Error generating ZaloPay QR code (Writer/Argument): ", e);
            Toast.makeText(this, "Lỗi tạo mã QR thanh toán", Toast.LENGTH_SHORT).show();
            binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert);
        } catch (Exception e) { // Bắt lỗi khác (ví dụ bitmap null)
            Log.e(TAG, "Unexpected error in generateAndDisplayQrCode", e);
            Toast.makeText(this, "Lỗi không xác định khi tạo QR", Toast.LENGTH_SHORT).show();
            binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert);
        }
    }

    // --- Hàm tạo chữ ký HMAC SHA256 ---
    public static String hmacSha256(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String hex = formatter.toString();
        formatter.close();
        return hex;
    }

    // Hàm ẩn/hiện loading indicator
    private void showLoading(boolean isLoading) {
        if (binding != null && binding.progressBarLoading != null && binding.ivQrCode != null) {
            binding.progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // Chỉ ẩn QR khi loading, việc hiện lại sẽ do generateAndDisplayQrCode hoặc lỗi xử lý
            if (isLoading) {
                binding.ivQrCode.setVisibility(View.INVISIBLE);
            } else {
                binding.ivQrCode.setVisibility(View.VISIBLE); // Hiện lại ImageView khi hết loading
            }
        }
        Log.d(TAG, "showLoading: " + isLoading);
    }

    // --- Hàm bắt đầu đếm ngược ---
    private void startCountdownTimer() {
        stopCountdownTimer(); // Hủy timer cũ trước khi bắt đầu timer mới
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(minutes);
                String timeFormatted = String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds);
                if (binding != null) binding.tvTimer.setText(timeFormatted); // Kiểm tra binding trước khi truy cập
            }
            @Override
            public void onFinish() {
                Log.d(TAG,"Countdown Timer Finished.");
                stopPollingOrderStatus(); // Dừng polling
                if (binding != null) {
                    binding.tvTimer.setText("Hết hạn");
                    binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert); // Hiển thị lỗi/hết hạn
                }
                Toast.makeText(ThanhToanQRActivity.this, "Giao dịch đã hết hạn", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    // Hàm dừng Countdown Timer
    private void stopCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null; // Gán null sau khi hủy
        }
    }


    // --- Hàm bắt đầu polling ---
    private void startPollingOrderStatus(String appTransIdToQuery) {
        Log.d(TAG, "Starting status polling for: " + appTransIdToQuery);
        if (pollingHandler == null) { // Kiểm tra handler trước khi dùng
            Log.e(TAG, "Polling Handler is null, cannot start polling!");
            return;
        }
        // Định nghĩa Runnable nếu chưa có
        if (queryStatusRunnable == null) {
            queryStatusRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!shouldPoll) {
                        Log.d(TAG, "Polling stopped (runnable check).");
                        return;
                    }
                    Log.d(TAG, "Runnable: Querying status...");
                    queryZaloPayOrderStatusInternal(appTransIdToQuery, this);
                }
            };
        }
        // Bắt đầu lần chạy đầu tiên
        pollingHandler.post(queryStatusRunnable);
    }

    // --- Hàm dừng polling ---
    private void stopPollingOrderStatus() {
        Log.d(TAG, "Stopping status polling.");
        shouldPoll = false;
        if (pollingHandler != null && queryStatusRunnable != null) {
            pollingHandler.removeCallbacks(queryStatusRunnable);
        }
        // Không cần gán runnable = null vì có thể cần dùng lại nếu logic thay đổi
    }

    // --- Hàm truy vấn trạng thái (INTERNAL) ---
    private void queryZaloPayOrderStatusInternal(String appTransIdToQuery, Runnable runnableToReschedule) {
        String dataToSign = ZP_APP_ID + "|" + appTransIdToQuery + "|" + ZP_KEY1;
        Log.d(TAG, "Query Internal - Data to sign: " + dataToSign);

        try {
            String mac = hmacSha256(ZP_KEY1, dataToSign);
            Log.d(TAG, "Query Internal - Generated MAC: " + mac);

            FormBody formBody = new FormBody.Builder()
                    .add("app_id", ZP_APP_ID)
                    .add("app_trans_id", appTransIdToQuery)
                    .add("mac", mac)
                    .build();

            Request request = new Request.Builder()
                    .url(ZP_QUERY_STATUS_ENDPOINT)
                    .post(formBody)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        Log.e(TAG, "Query Internal Failed (Network): ", e);
                        // Thử lại sau khoảng thời gian dài hơn nếu vẫn cần poll
                        if (shouldPoll && pollingHandler != null) {
                            pollingHandler.postDelayed(runnableToReschedule, POLLING_INTERVAL_MS * 2);
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    final String responseBody = response.body() != null ? response.body().string() : null;
                    if (response.body() != null) response.body().close();
                    Log.d(TAG, "Query Internal Response: " + responseBody);

                    runOnUiThread(() -> {
                        if (!isDestroyed() && response.isSuccessful() && responseBody != null) { // Kiểm tra Activity còn tồn tại
                            try {
                                JSONObject zpResponse = new JSONObject(responseBody);
                                int returnCode = zpResponse.getInt("return_code");
                                String returnMessage = zpResponse.getString("return_message");
                                long amountReceived = zpResponse.optLong("amount");

                                Log.d(TAG,"Query Internal Result: return_code=" + returnCode + ", message=" + returnMessage);

                                // --- Thanh toán Thành công ---
                                if (returnCode == 1) {

                                    Log.d(TAG, "Danh sách đơn hàng đã đặt: " + danhSachDonDatHang);

                                    Log.d(TAG, "ThanhTien: " + ThanhTien);

                                    xuLyDonHangSauThanhToan(newMaDH, maNH, maKH, KM, phiVC, ptThanhToan, TongTien, ThanhTien, thoiGianDatHang);


                                    stopPollingOrderStatus(); // Dừng polling
                                    Log.i(TAG, "Payment Successful! Stopping poll.");
                                    stopCountdownTimer(); // Dừng cả timer chính
                                    if(binding!=null) { // Kiểm tra binding trước khi cập nhật UI
                                        binding.tvTimer.setText("Đã thanh toán");
                                        binding.ivQrCode.setImageResource(android.R.drawable.checkbox_on_background);
                                    }
                                    showStatusResultDialog(returnCode, returnMessage, amountReceived);
                                } else if (returnCode == 2) { // Thất bại
                                    stopPollingOrderStatus();
                                    Log.w(TAG, "Payment Failed! Stopping poll.");
                                    stopCountdownTimer();
                                    if(binding!=null) {
                                        binding.tvTimer.setText("Thất bại");
                                        binding.ivQrCode.setImageResource(android.R.drawable.ic_dialog_alert);
                                    }
                                    showStatusResultDialog(returnCode, returnMessage, amountReceived);
                                } else if (returnCode == 3) { // Đang chờ xử lý
                                    if (shouldPoll && pollingHandler != null) { // Kiểm tra cả handler
                                        Log.d(TAG,"Payment Pending. Rescheduling query...");
                                        pollingHandler.postDelayed(runnableToReschedule, POLLING_INTERVAL_MS);
                                    }
                                } else { // Lỗi khác
                                    stopPollingOrderStatus();
                                    Log.e(TAG,"Query Status Failed (ZaloPay Unknown Error): " + returnCode + " - " + returnMessage);
                                    Toast.makeText(ThanhToanQRActivity.this, "Lỗi kiểm tra ZaloPay: " + returnMessage, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                Log.e(TAG, "Query Internal Failed (JSON Parse): ", e);
                                if (shouldPoll && pollingHandler != null) { // Thử lại nếu lỗi parse
                                    pollingHandler.postDelayed(runnableToReschedule, POLLING_INTERVAL_MS * 2);
                                }
                            }
                        } else if (!isDestroyed()) { // Chỉ xử lý lỗi HTTP nếu Activity chưa bị hủy
                            Log.e(TAG, "Query Internal Failed (HTTP): " + (response != null ? response.code() : "Response is null"));
                            if (shouldPoll && pollingHandler != null) { // Thử lại nếu lỗi HTTP
                                pollingHandler.postDelayed(runnableToReschedule, POLLING_INTERVAL_MS * 2);
                            }
                        }
                    });
                }
            });

        } catch (Exception e) { // Bắt lỗi chung khi chuẩn bị query request
            Log.e(TAG, "Error preparing Query Status request: ", e);
            if (shouldPoll && pollingHandler != null) { // Thử lại nếu lỗi chuẩn bị
                pollingHandler.postDelayed(runnableToReschedule, POLLING_INTERVAL_MS * 2);
            }
        }
    }


    // --- Hàm hiển thị Dialog kết quả ---
    private void showStatusResultDialog(int returnCode, String message, long amount) {
        // Kiểm tra Activity còn tồn tại trước khi hiển thị Dialog
        if (isFinishing() || isDestroyed()) {
            Log.w(TAG, "Activity is finishing/destroyed, cannot show status dialog.");
            return;
        }

        String title;
        String displayMessage = "Trạng thái: " + message;
        int iconResourceId; // Biến để lưu ID của icon

        if (returnCode == 1) { // Thành công
            title = "Thanh toán thành công";
            displayMessage += "\nSố tiền: " + String.format(Locale.getDefault(), "%,d", amount) + " VNĐ";
            // Sử dụng icon check (có thể cần icon khác đẹp hơn trong drawable của bạn)
            iconResourceId = android.R.drawable.ic_dialog_info; // Hoặc R.drawable.ic_check_circle nếu bạn có
        } else if (returnCode == 2) { // Thất bại
            title = "Thanh toán thất bại";
            // Sử dụng icon cảnh báo/lỗi
            iconResourceId = android.R.drawable.ic_dialog_alert; // Hoặc R.drawable.ic_error nếu bạn có
        } else { // Trạng thái khác (ví dụ: đang xử lý - dù hàm này thường gọi khi có kết quả cuối)
            title = "Thông báo";
            iconResourceId = android.R.drawable.ic_dialog_info;
        }

        // Sử dụng MaterialAlertDialogBuilder
        new MaterialAlertDialogBuilder(this) // Sử dụng Material design
                .setTitle(title)             // Đặt tiêu đề
                .setMessage(displayMessage)    // Đặt nội dung
                .setIcon(iconResourceId)       // Đặt icon
                .setCancelable(false)          // Không cho hủy ngang bằng nút back hoặc chạm ngoài
                .setPositiveButton("Đóng", (dialog, which) -> {
                    // Xử lý khi nhấn nút "Đóng"
                    dialog.dismiss(); // Đóng dialog hiện tại

                    // --- Điều hướng về màn đơn hàng (DonHangActivity) ---
                    Log.d(TAG, "Dialog dismissed. Navigating back to MainActivity.");
                    Intent intent = new Intent(ThanhToanQRActivity.this, DonHangActivity.class); // <<< Đảm bảo MainActivity là đúng màn hình chính
                    // Cờ này giúp xóa các Activity phía trên MainActivity và dùng lại instance cũ nếu có
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish(); // Đóng ThanhToanActivity sau khi đã khởi chạy Intent về MainActivity
                    // ----------------------------------------------------
                })
                .show(); // Hiển thị dialog
    }

    // --- Các hàm phụ trợ ---
    private void setupHelpLink() {
        binding.tvHelpLink.setOnClickListener(v -> {
            String helpUrl = "https://docs.zalopay.vn/"; // Hoặc link cụ thể hơn
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(helpUrl));
            try {
                startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở link hướng dẫn", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error opening help link", e);
            }
        });
    }

    private void setupStaticImages() {
        // Ví dụ: Nếu bạn có ảnh logo và bank trong drawable
        // binding.ivMerchantLogo.setImageResource(R.drawable.logo_merchant_demo);
        // binding.ivBankLogos.setImageResource(R.drawable.bank_logos_vietqr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPollingOrderStatus(); // Dừng polling khi Activity hủy
        stopCountdownTimer();    // Dừng cả timer chính
        Log.d(TAG, "onDestroy called - Timers and polling stopped.");
        // binding = null; // Không cần thiết với ViewBinding Activity lifecycle
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    // --- Sau khi thanh toán thành công ---
    // 1. Xóa Đơn hàng trong ChiTietGioHang
    // 2. Tạo 1 document mới tại DonHang với tên DH06 trạng thái "Chờ xác nhận" để tiến hành ship hàng
    // 3. Tạo 1 document mới tại ChiTietDonHang với tên CTDH_DH06_MA03

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

//  --- Lấy danh sách đơn hàng đã đặt để thêm vào chi tiết đơn hàng
    ArrayList<ChiTietGioHang> danhSachDonDatHang = new ArrayList<>();

    // Interface cho callback khi lấy chi tiết giỏ hàng xong
    interface OnChiTietGioHangFetchedListener {
        void onFetched(ArrayList<ChiTietGioHang>รายการGioHang);
        void onError(Exception e);
    }

    // Interface chung cho các thao tác Firestore khác
    interface OnFirestoreCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    private void xuLyDonHangSauThanhToan(String maDonMoi, String currentMaNH, String currentMaKH,
                                         double kmValue, double phiVCValue, String ptTTValue,
                                         double tongTienValue, double thanhTienValue, String thoiGianDatValue) {
        showLoading(true); // Hiển thị loading cho toàn bộ quá trình xử lý đơn hàng

        Log.d(TAG, "thanhTienValue: " + thanhTienValue);
        // Bước 1: Lấy danh sách chi tiết giỏ hàng hiện tại
        layChiTietGioHangHienTai(currentMaNH, currentMaKH, new OnChiTietGioHangFetchedListener() {
            @Override
            public void onFetched(ArrayList<ChiTietGioHang>GioHangDaLay) {
                if (GioHangDaLay == null ||GioHangDaLay.isEmpty()) {
                    Log.e(TAG, "Giỏ hàng trống hoặc lỗi khi lấy chi tiết giỏ hàng sau thanh toán.");
                    Toast.makeText(ThanhToanQRActivity.this, "Không thể xử lý đơn hàng: giỏ hàng trống.", Toast.LENGTH_LONG).show();
                    showLoading(false);
                    // Cân nhắc hiển thị dialog lỗi ở đây thay vì dialog thành công mặc định
                    return;
                }

                // Bước 2: Tạo đơn hàng chính
                taoDonHangChinhFirestore(maDonMoi, currentMaKH, currentMaNH, kmValue, phiVCValue, ptTTValue,
                        tongTienValue, thanhTienValue, thoiGianDatValue, new OnFirestoreCompleteListener() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "Tạo đơn hàng chính thành công.");
                                // Bước 3: Tạo chi tiết đơn hàng và cập nhật sản phẩm
                                taoChiTietDonHangVaCapNhatSanPhamFirestore(maDonMoi,GioHangDaLay, new OnFirestoreCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "Tạo chi tiết đơn hàng và cập nhật sản phẩm thành công.");
                                        // Bước 4: Xóa giỏ hàng
                                        xoaGioHangFirestore(currentMaKH, currentMaNH, new OnFirestoreCompleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d(TAG, "Xóa giỏ hàng thành công.");
                                                showLoading(false);
                                                Toast.makeText(ThanhToanQRActivity.this, "Đặt hàng và xử lý thành công!", Toast.LENGTH_LONG).show();
                                                // Mọi thứ đã xong, showStatusResultDialog đã được gọi ở luồng chính ZaloPay
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                showLoading(false);
                                                Log.e(TAG, "Lỗi khi xóa giỏ hàng: ", e);
                                                Toast.makeText(ThanhToanQRActivity.this, "Đặt hàng thành công, có lỗi khi dọn dẹp giỏ hàng.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        showLoading(false);
                                        Log.e(TAG, "Lỗi khi tạo chi tiết đơn hàng hoặc cập nhật sản phẩm: ", e);
                                        Toast.makeText(ThanhToanQRActivity.this, "Lỗi xử lý chi tiết đơn hàng sau thanh toán.", Toast.LENGTH_SHORT).show();
                                        // Cân nhắc: rollback đơn hàng chính?
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) {
                                showLoading(false);
                                Log.e(TAG, "Lỗi khi tạo đơn hàng chính sau thanh toán: ", e);
                                Toast.makeText(ThanhToanQRActivity.this, "Lỗi tạo đơn hàng chính sau thanh toán.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onError(Exception e) {
                showLoading(false);
                Log.e(TAG, "Lỗi nghiêm trọng khi lấy chi tiết giỏ hàng sau thanh toán: ", e);
                Toast.makeText(ThanhToanQRActivity.this, "Lỗi lấy thông tin giỏ hàng để xử lý đơn.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Hàm mới để lấy chi tiết giỏ hàng và dùng callback
    private void layChiTietGioHangHienTai(String maNH, String makhDN, OnChiTietGioHangFetchedListener listener) {
        CollectionReference refChiTietGioHang = firestore.collection("ChiTietGioHang");
        ArrayList<ChiTietGioHang> gioHangTam = new ArrayList<>(); // Tạo list cục bộ

        // Xóa dữ liệu cũ trong danhSachDonDatHang toàn cục nếu bạn vẫn muốn dùng nó
        // Hoặc tốt hơn là chỉ dùng gioHangTam và truyền đi
        if (ThanhToanQRActivity.this.danhSachDonDatHang != null) {
            ThanhToanQRActivity.this.danhSachDonDatHang.clear();
        } else {
            ThanhToanQRActivity.this.danhSachDonDatHang = new ArrayList<>(); // Đảm bảo nó được khởi tạo
        }


        refChiTietGioHang
                .whereEqualTo("MaNH", maNH)
                .whereEqualTo("MaKH", makhDN)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                ChiTietGioHang chiTietGioHang = doc.toObject(ChiTietGioHang.class);
                                gioHangTam.add(chiTietGioHang);
                                ThanhToanQRActivity.this.danhSachDonDatHang.add(chiTietGioHang); // Thêm vào biến toàn cục
                            }
                            listener.onFetched(gioHangTam); // Trả về danh sách vừa lấy được
                        } else {
                            // Snapshot null, trả về danh sách rỗng
                            listener.onFetched(gioHangTam);
                        }
                    } else {
                        Log.e("FirebaseError", "Lỗi khi truy vấn giỏ hàng: ", task.getException());
                        listener.onError(task.getException());
                    }
                });
    }

    private void taoDonHangChinhFirestore(String maDon, String makhDN, String maNHMoi,
                                          double kmIntent, double phiVCIntent, String ptThanhToanIntent,
                                          double tongTienIntent, double thanhTienIntent, String thoiGianDatIntent,
                                          OnFirestoreCompleteListener listener) {
        CollectionReference referenceDH = firestore.collection("DonHang");

        Map<String, Object> donHangMoi = new HashMap<>();
        donHangMoi.put("MaDon", maDon);
        donHangMoi.put("MaKH", makhDN);
        donHangMoi.put("MaNH", maNHMoi);
        donHangMoi.put("KhuyenMai", kmIntent); // Sử dụng biến từ Intent
        donHangMoi.put("KiemTraDonHang", false);
        donHangMoi.put("PhiShip", phiVCIntent); // Sử dụng biến từ Intent
        donHangMoi.put("PhuongThucTT", ptThanhToanIntent); // Sử dụng biến từ Intent
        donHangMoi.put("TrangThai", "Chờ xác nhận");
        donHangMoi.put("TrangThaiShip", "Chờ shipper xác nhận");
        donHangMoi.put("MaShipper", "");
        donHangMoi.put("TongTien", tongTienIntent); // Sử dụng biến từ Intent
        donHangMoi.put("ThanhTien", thanhTienIntent); // Sử dụng biến từ Intent
        donHangMoi.put("ThoiGianDatHang", thoiGianDatIntent); // Sử dụng biến từ Intent
        donHangMoi.put("ThoiGianGiaoHang", "");
        donHangMoi.put("TrangThaiDanhGia", "Chưa đánh giá");

        referenceDH.document(maDon).set(donHangMoi)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Lỗi khi thêm đơn hàng chính", e);
                    listener.onFailure(e);
                });
    }

    private void taoChiTietDonHangVaCapNhatSanPhamFirestore(String maDon, ArrayList<ChiTietGioHang> itemsTrongGio,
                                                            OnFirestoreCompleteListener listener) {
        if (itemsTrongGio == null || itemsTrongGio.isEmpty()) {
            Log.w(TAG, "Không có items để thêm vào chi tiết đơn hàng.");
            listener.onSuccess(); // Coi như thành công vì không có gì để làm
            return;
        }

        CollectionReference referenceCTDH = firestore.collection("ChiTietDonHang");
        CollectionReference referenceMonAn = firestore.collection("MonAn");
        List<Task<?>> allFirestoreTasks = new ArrayList<>();

        for (ChiTietGioHang item : itemsTrongGio) {
            Map<String, Object> ctdh = new HashMap<>();
            ctdh.put("MaDon", maDon);
            ctdh.put("Anh", item.getAnh());
            ctdh.put("Gia", item.getGiaMon());
            ctdh.put("MaMon", item.getMaMon());
            ctdh.put("SoLuong", item.getSoLuong()); // Quan trọng: Số lượng mua
            ctdh.put("TenMon", item.getTenMon());

            // Task 1: Thêm chi tiết đơn hàng
            Task<Void> addDetailTask = referenceCTDH.document("CTDH_" + maDon + "_" + item.getMaMon()).set(ctdh);
            allFirestoreTasks.add(addDetailTask);

            // Task 2: Cập nhật món ăn (lấy thông tin món, rồi cập nhật)
            Task<Void> updateProductTask = referenceMonAn.whereEqualTo("MaMon", item.getMaMon())
                    .get()
                    .continueWithTask(task -> {
                        if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                            Log.e("Firebase", "Không tìm thấy MonAn với MaMon: " + item.getMaMon() + " để cập nhật.");
                            // Trả về một task đã hoàn thành (không làm gì) để không block Tasks.whenAllComplete
                            return Tasks.forResult(null);
                        }

                        List<Task<Void>> monAnUpdateSubTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            MonAn monAn = doc.toObject(MonAn.class);
                            double slt = monAn.getSoLuongTon();
                            double slb = monAn.getSoLuotBan();
                            double soLuongMua = item.getSoLuong(); // Lấy số lượng thực mua

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("SoLuongTon", slt - soLuongMua);
                            updates.put("SoLuotBan", slb + soLuongMua);

                            if (slt - soLuongMua <= 0) {
                                updates.put("TrangThai", false); // Hết hàng
                            }
                            monAnUpdateSubTasks.add(doc.getReference().update(updates));
                        }
                        return Tasks.whenAll(monAnUpdateSubTasks); // Gộp các task update cho món này
                    });
            allFirestoreTasks.add(updateProductTask);
        }

        // Chờ tất cả các task (thêm chi tiết + cập nhật món ăn) hoàn thành
        Tasks.whenAllComplete(allFirestoreTasks).addOnCompleteListener(combinedTask -> {
            boolean allSucceeded = true;
            for (Task<?> taskResult : combinedTask.getResult()) {
                if (!taskResult.isSuccessful()) {
                    allSucceeded = false;
                    Log.e(TAG, "Một task trong tạo CTĐH hoặc cập nhật SP thất bại: ", taskResult.getException());
                }
            }
            if (allSucceeded) {
                listener.onSuccess();
            } else {
                listener.onFailure(new Exception("Một hoặc nhiều thao tác chi tiết đơn hàng/cập nhật sản phẩm thất bại."));
            }
        });
    }

    private void xoaGioHangFirestore(String makhDN, String maNH, OnFirestoreCompleteListener listener) {
        CollectionReference refChiTietGioHang = firestore.collection("ChiTietGioHang");

        refChiTietGioHang.whereEqualTo("MaKH", makhDN)
                .whereEqualTo("MaNH", maNH) // Thêm điều kiện MaNH nếu cần
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot == null || snapshot.isEmpty()) {
                            Log.d(TAG, "Giỏ hàng đã trống, không cần xóa.");
                            listener.onSuccess();
                            return;
                        }
                        List<Task<Void>> deleteTasks = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshot) {
                            deleteTasks.add(doc.getReference().delete());
                        }
                        // Chờ tất cả các task xóa hoàn thành
                        Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(deleteCombinedTask -> {
                            boolean allDeleted = true;
                            for(Task<?> t : deleteCombinedTask.getResult()){
                                if(!t.isSuccessful()){
                                    allDeleted = false;
                                    Log.e(TAG, "Lỗi khi xóa một mục trong giỏ hàng: ", t.getException());
                                }
                            }
                            if(allDeleted){
                                listener.onSuccess();
                            } else {
                                listener.onFailure(new Exception("Lỗi khi xóa một số mục trong giỏ hàng."));
                            }
                        });
                    } else {
                        Log.e("FirebaseError", "Lỗi khi truy vấn giỏ hàng để xóa: ", task.getException());
                        listener.onFailure(task.getException());
                    }
                });
    }
}