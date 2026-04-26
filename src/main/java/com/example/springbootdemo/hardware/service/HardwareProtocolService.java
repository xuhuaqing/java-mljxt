package com.example.springbootdemo.hardware.service;

import com.example.springbootdemo.hardware.HardwareControlRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class HardwareProtocolService {

    public static final byte FRAME_HEAD_1 = (byte) 0xAA;
    public static final byte FRAME_HEAD_2 = (byte) 0xBB;
    public static final byte FRAME_LENGTH = 0x14;

    public byte[] buildFrame(HardwareControlRequest request) {
        byte[] frame = new byte[23];
        frame[0] = FRAME_HEAD_1;
        frame[1] = FRAME_HEAD_2;
        frame[2] = FRAME_LENGTH;

        byte[] customerBytes = request.getCustomerId().getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(customerBytes, 0, frame, 3, 11);

        frame[14] = intToByte(0x30 + request.getGender());
        frame[15] = intToByte(request.getHeight());
        frame[16] = intToByte(request.getAge());
        frame[17] = intToByte(request.getWeight());
        frame[18] = intToByte(request.getProjectCode());
        frame[19] = intToByte(request.getProjectMinutes());
        frame[20] = intToByte(0x30 + request.getSportPerformance());
        frame[21] = intToByte(request.getUsageCount());
        frame[22] = checksum(frame, 3, 21);
        return frame;
    }

    public String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 3);
        for (byte value : bytes) {
            builder.append(String.format(Locale.ROOT, "%02X ", value));
        }
        return builder.toString().trim();
    }

    private byte checksum(byte[] frame, int start, int end) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += frame[i] & 0xFF;
        }
        return (byte) (sum & 0xFF);
    }

    private byte intToByte(int value) {
        return (byte) (value & 0xFF);
    }
}
