<template>
  <div class="shift">
    <h3>Shifts</h3>
    <table id="firstTable">
      <thead>
      <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Shift Date</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="row in shifts">
        <td>{{row.id}}</td>
        <td>{{row.worker.lastName}} {{row.worker.firstName}}</td>
        <td>{{row.shiftDate}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import axios from 'axios';
  import Worker from 'Worker.vue';

  export interface Shift {
    id: number;
    worker: Worker;
    shiftDate: Date;
  }
  @Component({
    components: {
    }
  })

  export default class Shifts extends Vue {
    public shifts: Shift[] = [];

    private async created() {
      const response = await axios.get('/api/shifts');
      this.shifts = await response.data;
    }
  }
</script>
