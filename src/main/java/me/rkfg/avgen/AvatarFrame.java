package me.rkfg.avgen;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("serial")
public class AvatarFrame extends Frame {

    private class AvatarData {
        public AvatarData(byte[] directions, Color color) {
            this.directions = directions;
            this.color = color;
        }

        public byte[] directions;
        public Color color;
    }

    private int[][] avatar;
    private int qstep = 10;
    private Color color;
    private int lastx, lasty;
    private boolean animate;

    public AvatarFrame(String name, Integer size, boolean animate) {
        this.animate = animate;
        setTitle("Avatar for " + name);
        avatar = new int[size][size];
        setSize(avatar[0].length * qstep, avatar.length * qstep);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        try {
            initData(name);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
    }

    private void initData(String name) throws NoSuchAlgorithmException {
        AvatarData data = getData(name);
        color = data.color;
        fillAvatarData(data);
    }

    public void fillAvatarData(AvatarData avatarData) {
        int y = avatar.length / 2;
        int x = avatar[y].length / 2;
        for (int i = 0; i < avatarData.directions.length; ++i) {
            switch (avatarData.directions[i]) {
            case 0:
                x++;
                break;
            case 1:
                x--;
                break;
            case 2:
                y++;
                break;
            case 3:
                y--;
                break;
            case 4:
                x++;
                y++;
                break;
            case 5:
                x--;
                y--;
                break;
            case 6:
                x++;
                y--;
                break;
            case 7:
                x--;
                y++;
                break;
            default:
                break;
            }
            if (x < 0) {
                x += avatar[0].length;
            }
            if (x >= avatar[0].length) {
                x -= avatar[0].length;
            }
            if (y < 0) {
                y += avatar.length;
            }
            if (y >= avatar.length) {
                y -= avatar.length;
            }
            avatar[x][y]++;
            avatar[avatar[0].length - x - 1][y]++;
            if (animate) {
                lastx = x;
                lasty = y;
                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        lastx = -1;
        lasty = -1;
        repaint();
    }

    private AvatarData getData(String name) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("sha-256");
        digest.update(name.getBytes());
        byte[] sha256 = digest.digest();
        byte[] result = new byte[83];
        int bitpos = 0;
        int bytepos = 1;
        byte curdir = 0;
        byte nextByte = 0;
        int ri = 0;
        while (bytepos < sha256.length) {
            byte carriedBits = (byte) (8 - bitpos);
            if (bytepos < sha256.length - 1) {
                nextByte = sha256[bytepos + 1];
            } else {
                nextByte = 0;
            }
            final int carriedMask = 0xff >> carriedBits;
            final int curMask = 0xff >> bitpos;
            byte nbPart = (byte) (nextByte & carriedMask);
            final int nbPartShifted = nbPart << carriedBits;
            byte curbyte = (byte) (sha256[bytepos] >> bitpos & curMask | nbPartShifted);
            curdir = (byte) (curbyte & 7);
            bitpos += 3;
            if (bitpos > 7) {
                bitpos -= 8;
                bytepos++;
            }
            result[ri++] = curdir;
        }
        int ub = Byte.toUnsignedInt(sha256[0]);
        int r = (ub % 6) * 0x33;
        int g = (ub / 6 % 6) * 0x33;
        int b = (ub / 36 % 6) * 0x33;
        Color color = new Color(r, g, b);
        return new AvatarData(result, color);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (int y = 0; y < avatar.length; ++y) {
            for (int x = 0; x < avatar[y].length; ++x) {
                final int overdraw = avatar[x][y];
                if (overdraw > 0) {
                    if (lastx == x && lasty == y) {
                        g.setColor(Color.WHITE);
                    } else {
                        Color curColor = color;
                        for (int i = 1; i < overdraw; ++i) {
                            curColor = curColor.brighter();
                        }
                        g.setColor(curColor);
                    }
                    g.fillRect(x * qstep, y * qstep, qstep, qstep);
                    /*
                     * g.setColor(Color.WHITE); g.drawString("" + avatar[x][y], x * qstep, y * qstep + qstep);
                     */ }
            }
        }
    }
}
