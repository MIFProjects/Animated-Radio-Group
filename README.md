# Animated-Radio-Group
# The simple android library with nice animations.

## Features:
  - Diffrent type of animations by default
  - Add custom anniation
  - Easily custom component
  - Works like LinearLayout
  - Can contains different child elements
  
![demo](screenshots/preview.gif)

# Usege
## Dependency:
```
dependencies {
    compile 'com.github.MIFProjects:Animated-Radio-Group:1.0.2'
}

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
        ...
    }
}
```
# XML Usage
```xml

 <com.mif.animatedradiogrpouplib.AnimatedRadioGroup
            android:id="@+id/animatedWrapContentWithFixContent"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            app:circleCenterFillRadius="13dp"
            app:circleFillColor="@color/colorPrimary"
            app:circleGravity="center"
            app:circlePaddingLeft="15dp"
            app:circleRadius="17dp"
            app:circleStrokeColor="@color/colorPrimaryDark"
            app:circleStrokeWidth="3dp"
            app:separatorColor="@color/colorDivider"
            app:separatorMargitEnd="15dp"
            app:separatorMargitStart="15dp"
            app:setFullItemForClick="false"
            app:setSeparator="true">

            // Just add some layouts

</com.mif.animatedradiogrpouplib.AnimatedRadioGroup>
```

## Java Usage
 ```java
         //Create Object and refer to layout view
        AnimatedRadioGroup animatedWrapContent = (AnimatedRadioGroup) view.findViewById(R.id.animatedWrapContent);
        // Set radius of circle
        animatedWrapContent.setCircleCenterFillRadius((int) getResources().getDimension(R.dimen.circleFillRadius));
        // Set color of circle
        animatedWrapContent.setCircleFillColor(getResources().getColor(R.color.colorPrimary));
        // Set gravity of radio button
        animatedWrapContent.setCircleGravity(Gravity.CENTER);
        // Set padding of radio button
        animatedWrapContent.setCirclePaddingLeft((int) getResources().getDimension(R.dimen.circlePaddingLeft));
        animatedWrapContent.setCirclePaddingRight((int) getResources().getDimension(R.dimen.circlePaddingLeft));
        // Set radius of radio button
        animatedWrapContent.setCircleRadius((int) getResources().getDimension(R.dimen.circleRadius));
        // Set color of circle stroke
        animatedWrapContent.setCircleStrokeColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set width of circle scroke
        animatedWrapContent.setCircleStrokeWidth((int)getResources().getDimension(R.dimen.circleStrokeWidth));
        // Set color of separator
        animatedWrapContent.setSeparatorColor(getResources().getColor(R.color.colorDivider));
        // Set margin of separator
        animatedWrapContent.setSeparatorMarginEnd((int)getResources().getDimension(R.dimen.separatorMargin));
        animatedWrapContent.setSeparatorMarginStart((int)getResources().getDimension(R.dimen.separatorMargin));
        // Set width of separator
        animatedWrapContent.setSeparatorStrokeWidth((int)getResources().getDimension(R.dimen.separatorStrokeWidth));
        // Сlick for the entire element
        animatedWrapContent.setFullItemForClick(false);
        // Set showing of separator
        animatedWrapContent.setSeparator(false);

 ```


License
 -------

     Copyright 2018 MIFProjects

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
