<template>
  <section>
    <div class="form-check">
      <input class="form-check-input" type="checkbox" v-model="showMarkers" id="showTweets">
      <label class="form-check-label" for="showTweets">
        Show Tweets
      </label>
    </div>

    <gmap-map
      :center="getCenter"
      :zoom="7"
      style="width:100%;  height: 400px;"
    >
      <gmap-marker
        :key="index"
        v-for="(m, index) in getCoordinates"
        :position="m.position"
        :opacity="0.5"
      >
        <gmap-info-window :opened="showMarkers">
          {{ data[index].text }}
        </gmap-info-window>
      </gmap-marker>
      <gmap-circle
        :center="getCenter"
        :radius="160934"
        :opacity="0.1"
        :draggable="false"
        :editable="false"
      />
    </gmap-map>
  </section>
</template>

<script>
export default {
  props: ['data', 'centerLat', 'centerLng'],
  data: function() {
    return {
      markers: [
        {
          position: {
            lat: 33.9753618,
            lng: -117.3261623
          }
        }
      ],
      showMarkers: true
    };
  },
  computed: {
    getCoordinates() {
      let coordinates = [];

      this.data.forEach(tweet => {
        let point = {};
        point.lat = tweet.location.lat;
        point.lng = tweet.location.lng;

        coordinates.push({ position: point });
      });
      return coordinates;
    },
    getCenter() {
      let center = {};

      center.lat = this.centerLat;
      center.lng = this.centerLng;

      return center;
    }
  }
};
</script>

<style>
.gm-style-iw {
  color: black;
}
</style>
