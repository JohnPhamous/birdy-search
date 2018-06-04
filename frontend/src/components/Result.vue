<template>
  <div class="tweet">
    <div class="card">
      <div class="card-header">
        <h4>
          <a
            :href="`https://twitter.com/${tweet.user.screen_name}`"
            class="badge badge-pill badge-dark"
            target="_blank"
          >
            @{{ tweet.user.screen_name }}
          </a>
          <span class="date">
            <a :href="`https://twitter.com/${tweet.user.screen_name}/status/${tweet.id_str}`" target="_blank">{{ getDate }}</a>
          </span>
        </h4>
      </div>
      <div class="card-body">
        <blockquote class="blockquote mb-0">
          <p v-html="getTweetBody"></p>
          <div>
            <a
              v-if="tweet.hashtags"
              v-for="h in tweet.hashtags"
              :key="h"
              :style="{ background: stringToColor(h) }"
              class="hashtag"
              :href="`https://twitter.com/hashtag/${h}`"
              target="_blank"
            >
              {{ h }}
            </a>
          </div>
        </blockquote>
      </div>
    </div>
  </div>
</template>

<script>
import Snowball from 'snowball';
const stemmer = new Snowball('English');

export default {
  props: ['tweet', 'query'],
  computed: {
    getDate() {
      const unix = this.tweet.timestamp;
      const date = new Date(unix);
      const time = date.toTimeString().substr(0, 5);
      const day = date.toDateString();

      return `${time} - ${day}`;
    },
    getTweetBody() {
      const tweet = this.tweet.text;
      const tokens = tweet.split(/[\s,]+/);
      //console.log(tokens);

      let newTweet = '';

      tokens.forEach(token => {
        stemmer.setCurrent(token);
        stemmer.stem();
        const currentTokenStem = stemmer.getCurrent().toLowerCase();

        stemmer.setCurrent(this.query.split('limit')[0].toLowerCase());
        stemmer.stem();

        const currentQuery = stemmer.getCurrent().toLowerCase();

        /*console.log(
          'Query: ' + this.query.split('limit')[0],
          'QueryStem: ' + currentQuery,
          'Token: ' + token,
          'TokenStem: ' + currentTokenStem,
        );*/
        if (
          token.substr(0, this.query.length).toLowerCase() === currentQuery ||
          currentQuery === currentTokenStem
        ) {
          newTweet += `<strong style="background: #afd1f6; padding: 0px 5px;">${token}</strong> `;
        } else if (token.includes('http')) {
          newTweet += `<a href="${token}">${token}</a>`;
          newTweet += ` <small><em>(${this.tweet.title})</em></small>`;
        } else if (token.includes('@')) {
          newTweet += `<a href="http://twitter.com/${token}" target="_blank">${token}</a> `;
        } else {
          if (token === '@') {
            newTweet += `${token}`;
          } else {
            newTweet += `${token} `;
          }
        }
      });
      return newTweet;
    }
  },
  methods: {
    stringToColor(str) {
      var hash = 0;
      for (var i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
      }
      var colour = '#';
      for (var i = 0; i < 3; i++) {
        var value = (hash >> (i * 8)) & 0xff;
        colour += ('00' + value.toString(16)).substr(-2);
      }
      return colour;
    }
  }
};
</script>

<style scoped>
.card {
  width: 80vw;
  text-align: left;
  margin-bottom: 10px;
}
.card,
p {
  color: black;
}
.date {
  float: right;
  font-size: 15px;
}
.hashtag {
  padding: 3px 6px;
  margin-left: 10px;
  color: black;
  text-align: center;
}
</style>
