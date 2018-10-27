
package Game;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 *
 * @author Baran Cetin
 */
public class Explosion extends Transition {
    ImageView explosionImg;
    Rectangle2D [] partialImage;
    
    public Explosion(ImageView explosionImg, Rectangle2D [] partialImg){
        this.explosionImg = explosionImg;
        this.partialImage = partialImg;
        this.setCycleDuration(Duration.seconds(1));
        this.setCycleCount(1);
        this.setRate(0.1);
        this.setInterpolator(Interpolator.LINEAR);
    }
    @Override
    protected void interpolate(double frac){
        int index = (int)(frac * 200);
        if (index < 20){
            explosionImg.setViewport(partialImage[index]);
        }
    }
}
