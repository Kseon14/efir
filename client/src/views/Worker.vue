<template>
  <div class="workers">
    <h3>Workers</h3>
      <table id="firstTable" class="worker">
        <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Base salary</th>
          <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in workers">
          <td>{{row.id}}</td>
          <td>{{row.lastName}} {{row.firstName}}</td>
          <td>{{row.baseSalary}}</td>
          <td>{{row.status}}</td>
        </tr>
        </tbody>
      </table>
    </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
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
  }
})

export default class Workers extends Vue {
  public workers: Worker[] = [];

  private async created() {
    const response = await axios.get('/api/workers');
    this.workers = await response.data;
  }
}
</script>
<style>
  table.worker {
    font-family: 'Open Sans', sans-serif;
    width: 40%;
    border-collapse: collapse;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    margin: 0 auto;
  }

  table.worker th {
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    padding: 5px;
    min-width: 20px;
    text-align: left;

  }

  table.worker td {
    padding: 5px;
    border-bottom: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
    text-align: left;
  }

</style>
