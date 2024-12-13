import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundManager {
    private Clip eatingSound;
    private Clip collisionSound;

    public SoundManager() {
        try {
            eatingSound = AudioSystem.getClip();
            eatingSound.open(AudioSystem.getAudioInputStream(new File("pow-90398.wav")));

            collisionSound = AudioSystem.getClip();
            collisionSound.open(AudioSystem.getAudioInputStream(new File("129219606-jingle-end-game.wav")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playEatingSound() {
        eatingSound.setFramePosition(0);
        eatingSound.start();
    }

    public void playCollisionSound() {
        collisionSound.setFramePosition(0);
        collisionSound.start();
    }
}