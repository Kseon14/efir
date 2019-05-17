<template>
  <div class="home">
    <h1>Workers</h1>
    <div v-for="worker in workers">
      {{ worker.firstName}}
      {{ worker.lastName}}
      {{ worker.baseSalary}}
      {{ worker.status}}
    </div>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import HelloWorld from '@/components/HelloWorld.vue' // @ is an alias to /src
  import axios from 'axios';

  export interface Worker {
  id: number;
  firstName: string;
  lastName: string;
  baseSalary: number;
  status: string;
}

@Component({
  components: {
    HelloWorld
  }
})

export default class Home extends Vue {
  public workers: Worker[] = [];

  private async created() {
    const response = await axios.get('/api/workers');
    this.workers = await response.data;
  }
}
</script>
